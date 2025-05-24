package ispw.project.project_ispw.dao.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.dao.ListAnime;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListAnimeDaoCsv implements ListAnime {

    private static final Logger LOGGER = Logger.getLogger(ListAnimeDaoCsv.class.getName());
    private static final String CSV_FILE_NAME;
    private static final String ANIME_CSV_FILE_NAME; // To get anime details

    // Static block to load CSV file names and ensure files exist
    static {
        Properties properties = new Properties();
        String listAnimeFileName = "list_anime.csv"; // Default
        String animeFileName = "anime.csv"; // Default for fetching anime details

        try (InputStream input = ListAnimeDaoCsv.class.getClassLoader().getResourceAsStream("csv.properties")) {
            if (input != null) {
                properties.load(input);
                listAnimeFileName = properties.getProperty("list_anime.csv.filename", listAnimeFileName);
                animeFileName = properties.getProperty("anime.csv.filename", animeFileName);
            } else {
                LOGGER.log(Level.WARNING, "csv.properties file not found. Using default CSV filenames.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading csv.properties for ListAnimeDaoCsv. Using default filenames.", e);
        }

        CSV_FILE_NAME = listAnimeFileName;
        ANIME_CSV_FILE_NAME = animeFileName;

        // Ensure both files exist
        try {
            if (!Files.exists(Paths.get(CSV_FILE_NAME))) {
                Files.createFile(Paths.get(CSV_FILE_NAME));
                LOGGER.log(Level.INFO, "List-Anime CSV file created: {0}", CSV_FILE_NAME);
            }
            if (!Files.exists(Paths.get(ANIME_CSV_FILE_NAME))) {
                Files.createFile(Paths.get(ANIME_CSV_FILE_NAME));
                LOGGER.log(Level.INFO, "Anime CSV file created for lookup: {0}", ANIME_CSV_FILE_NAME);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create CSV files for ListAnimeDaoCsv.", e);
            throw new RuntimeException("Initialization failed: Could not create CSV files.", e);
        }
    }

    public ListAnimeDaoCsv() {
        // Constructor no longer needs to create the file; the static block handles it.
    }

    @Override
    public void addAnimeToList(ListBean list, AnimeBean anime) throws ExceptionDao {
        try {
            // Check if the anime already exists in the list
            if (animeExistsInList(list, anime)) {
                throw new ExceptionDao("Anime ID " + anime.getIdAnimeTmdb() + " already exists in list ID " + list.getId() + ".");
            }

            // Add the anime to the CSV file
            // Using StandardOpenOption.APPEND for appending and NEW_BUFFERED_WRITER for efficiency
            try (CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME), StandardOpenOption.APPEND))) {
                String[] record = {String.valueOf(list.getId()), String.valueOf(anime.getIdAnimeTmdb())};
                csvWriter.writeNext(record);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error adding anime ID " + anime.getIdAnimeTmdb() + " to list ID " + list.getId() + " in CSV file.", e);
            throw new ExceptionDao("Failed to add anime to list in CSV. I/O or data error.", e);
        }
    }

    @Override
    public void removeAnimeFromList(ListBean list, AnimeBean anime) throws ExceptionDao {
        try {
            // Check if the anime exists in the list
            if (!animeExistsInList(list, anime)) {
                throw new ExceptionDao("Anime ID " + anime.getIdAnimeTmdb() + " not found in list ID " + list.getId() + ".");
            }

            List<String[]> allRecords = new ArrayList<>();
            // Read all records, excluding the one to be removed
            try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
                String[] record;
                while ((record = csvReader.readNext()) != null) {
                    if (record.length < 2) {
                        LOGGER.log(Level.WARNING, "Skipping malformed CSV record during removal: {0}", String.join(",", record));
                        continue;
                    }
                    if (!(record[0].equals(String.valueOf(list.getId())) && record[1].equals(String.valueOf(anime.getIdAnimeTmdb())))) {
                        allRecords.add(record);
                    }
                }
            }

            // Write the updated records back to the file (overwriting it)
            try (CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME)))) {
                csvWriter.writeAll(allRecords);
            }
        } catch (IOException | CsvValidationException e) {
            LOGGER.log(Level.SEVERE, "Error removing anime ID " + anime.getIdAnimeTmdb() + " from list ID " + list.getId() + " in CSV file.", e);
            throw new ExceptionDao("Failed to remove anime from list in CSV. I/O or data error.", e);
        }
    }

    @Override
    public List<AnimeBean> getAllAnimesInList(ListBean list) throws ExceptionDao {
        List<AnimeBean> animeList = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 2) {
                    LOGGER.log(Level.WARNING, "Skipping malformed CSV record while getting all animes in list: {0}", String.join(",", record));
                    continue;
                }
                if (record[0].equals(String.valueOf(list.getId()))) {
                    int animeId = Integer.parseInt(record[1]);
                    // Fetch Anime details from the anime.csv file
                    AnimeBean anime = fetchAnimeById(animeId);
                    if (anime != null) {
                        animeList.add(anime);
                    } else {
                        LOGGER.log(Level.WARNING, "Anime with ID {0} found in list {1} but not in {2}.",
                                new Object[]{animeId, list.getId(), ANIME_CSV_FILE_NAME});
                    }
                }
            }
        } catch (IOException | CsvValidationException | NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all animes for list ID " + list.getId() + " from CSV file.", e);
            throw new ExceptionDao("Failed to retrieve all animes for list from CSV. Data corruption or I/O error.", e);
        }

        if (animeList.isEmpty()) {
            LOGGER.log(Level.INFO, "No Anime Found in list ID: {0}", list.getId());
            // It might be valid for a list to be empty; throwing an exception here is optional.
            // Depending on business logic, an empty list might be preferred over an exception.
            // For now, mirroring previous behavior:
            throw new ExceptionDao("No Anime Found in the List (ID: " + list.getId() + ").");
        }

        return animeList;
    }

    // Helper method to check if an Anime is already in the List
    private boolean animeExistsInList(ListBean list, AnimeBean anime) throws ExceptionDao {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 2) {
                    LOGGER.log(Level.WARNING, "Skipping malformed CSV record during existence check: {0}", String.join(",", record));
                    continue;
                }
                if (record[0].equals(String.valueOf(list.getId())) && record[1].equals(String.valueOf(anime.getIdAnimeTmdb()))) {
                    return true;
                }
            }
        } catch (IOException | CsvValidationException e) {
            LOGGER.log(Level.SEVERE, "Error checking anime existence in list ID " + list.getId() + " for anime ID " + anime.getIdAnimeTmdb(), e);
            throw new ExceptionDao("Failed to check anime existence in list from CSV. I/O or data error.", e);
        }
        return false;
    }

    // Helper method to fetch an Anime by its ID from the separate anime.csv file
    private AnimeBean fetchAnimeById(int id) throws ExceptionDao {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(ANIME_CSV_FILE_NAME)))) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 4) { // Assuming ID, duration, episodes, title
                    LOGGER.log(Level.WARNING, "Skipping malformed Anime CSV record: {0}", String.join(",", record));
                    continue;
                }
                try {
                    if (Integer.parseInt(record[0]) == id) {
                        // Assuming AnimeBean constructor: public AnimeBean(int id, int duration, int episodes, String title)
                        return new AnimeBean(Integer.parseInt(record[0]), Integer.parseInt(record[2]), Integer.parseInt(record[1]), record[3]);
                    }
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Skipping CSV record with invalid number format: {0}", String.join(",", record));
                }
            }
        } catch (IOException | CsvValidationException e) {
            LOGGER.log(Level.SEVERE, "Error fetching anime ID " + id + " from " + ANIME_CSV_FILE_NAME, e);
            throw new ExceptionDao("Failed to fetch anime details from main anime CSV file. I/O or data error.", e);
        }
        return null;
    }
}