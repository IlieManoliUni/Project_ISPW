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

public class ListAnimeDaoCsv implements ListAnime {

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
                // Property file not found, use defaults
            }
        } catch (IOException e) {
            // Error loading properties, use defaults
        }

        CSV_FILE_NAME = listAnimeFileName;
        ANIME_CSV_FILE_NAME = animeFileName;

        // Ensure both files exist
        try {
            if (!Files.exists(Paths.get(CSV_FILE_NAME))) {
                Files.createFile(Paths.get(CSV_FILE_NAME));
            }
            if (!Files.exists(Paths.get(ANIME_CSV_FILE_NAME))) {
                Files.createFile(Paths.get(ANIME_CSV_FILE_NAME));
            }
        } catch (IOException e) {
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
                        continue; // Skip malformed records
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
            throw new ExceptionDao("Failed to remove anime from list in CSV. I/O or data error.", e);
        }
    }

    @Override
    public List<AnimeBean> getAllAnimeInList(ListBean list) throws ExceptionDao {
        List<AnimeBean> animeList = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 2) {
                    continue; // Skip malformed records
                }
                if (record[0].equals(String.valueOf(list.getId()))) {
                    int animeId = Integer.parseInt(record[1]);
                    // Fetch Anime details from the anime.csv file
                    AnimeBean anime = fetchAnimeById(animeId);
                    if (anime != null) {
                        animeList.add(anime);
                    } else {
                        // Anime found in list_anime.csv but not in anime.csv
                    }
                }
            }
        } catch (IOException | CsvValidationException | NumberFormatException e) {
            throw new ExceptionDao("Failed to retrieve all animes for list from CSV. Data corruption or I/O error.", e);
        }

        if (animeList.isEmpty()) {
            // It might be valid for a list to be empty; throwing an exception here is optional.
            // Depending on business logic, an empty list might be preferred over an exception.
            throw new ExceptionDao("No Anime Found in the List (ID: " + list.getId() + ").");
        }

        return animeList;
    }

    @Override
    public void removeAllAnimesFromList(ListBean list) throws ExceptionDao {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }

        try {
            List<String[]> allRecords = new ArrayList<>();
            boolean listHadEntries = false; // Flag to check if the list actually contained entries

            // Read all records, excluding those for the specified list ID
            try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
                String[] record;
                while ((record = csvReader.readNext()) != null) {
                    if (record.length < 2) {
                        continue; // Skip malformed records
                    }
                    // Keep records that do NOT match the list ID
                    if (!record[0].equals(String.valueOf(list.getId()))) {
                        allRecords.add(record);
                    } else {
                        listHadEntries = true; // Mark that we found entries for this list ID
                    }
                }
            }

            // Write the filtered records back to the file (overwriting it)
            try (CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME)))) {
                csvWriter.writeAll(allRecords);
            }

            if (!listHadEntries) {
                // If the list ID was not found in the CSV, throw an exception
                throw new ExceptionDao("List with ID " + list.getId() + " not found or already empty.");
            }

        } catch (IOException | CsvValidationException e) {
            throw new ExceptionDao("Failed to remove all anime from list in CSV. I/O or data error.", e);
        }
    }


    // Helper method to check if an Anime is already in the List
    private boolean animeExistsInList(ListBean list, AnimeBean anime) throws ExceptionDao {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 2) {
                    continue; // Skip malformed records
                }
                if (record[0].equals(String.valueOf(list.getId())) && record[1].equals(String.valueOf(anime.getIdAnimeTmdb()))) {
                    return true;
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new ExceptionDao("Failed to check anime existence in list from CSV. I/O or data error.", e);
        }
        return false;
    }

    // Helper method to fetch an Anime by its ID from the separate anime.csv file
    private AnimeBean fetchAnimeById(int id) throws ExceptionDao {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(ANIME_CSV_FILE_NAME)))) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 4) { // Assuming ID, episodes, duration, title (order in AnimeBean constructor is id, duration, episodes, title)
                    continue; // Skip malformed Anime CSV records
                }
                try {
                    if (Integer.parseInt(record[0]) == id) {
                        // Assuming AnimeBean constructor: public AnimeBean(int id, int duration, int episodes, String title)
                        // Make sure the indices match the CSV columns: record[0]=ID, record[1]=Episodes, record[2]=Duration, record[3]=Title
                        return new AnimeBean(Integer.parseInt(record[0]), Integer.parseInt(record[2]), Integer.parseInt(record[1]), record[3]);
                    }
                } catch (NumberFormatException e) {
                    // Skipping CSV record with invalid number format
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new ExceptionDao("Failed to fetch anime details from main anime CSV file. I/O or data error.", e);
        }
        return null;
    }
}