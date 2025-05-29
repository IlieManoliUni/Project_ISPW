package ispw.project.project_ispw.dao.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.dao.ListAnime;
import ispw.project.project_ispw.exception.CsvDaoException;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.io.IOException;
import java.io.InputStream;
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
    private static final String ANIME_CSV_FILE_NAME;

    static {
        Properties properties = new Properties();
        String listAnimeFileName = "list_anime.csv";
        String animeFileName = "anime.csv";

        try (InputStream input = ListAnimeDaoCsv.class.getClassLoader().getResourceAsStream("csv.properties")) {
            if (input != null) {
                properties.load(input);
                listAnimeFileName = properties.getProperty("list_anime.csv.filename", listAnimeFileName);
                animeFileName = properties.getProperty("anime.csv.filename", animeFileName);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load csv.properties. Using default CSV file names.", e);
        }

        CSV_FILE_NAME = listAnimeFileName;
        ANIME_CSV_FILE_NAME = animeFileName;

        try {
            if (!Files.exists(Paths.get(CSV_FILE_NAME))) {
                Files.createFile(Paths.get(CSV_FILE_NAME));
            }
            if (!Files.exists(Paths.get(ANIME_CSV_FILE_NAME))) {
                Files.createFile(Paths.get(ANIME_CSV_FILE_NAME));
            }
        } catch (IOException e) {
            throw new CsvDaoException("Initialization failed: Could not create CSV files.", e);
        }
    }

    public ListAnimeDaoCsv() {
        //Empty constructor
    }

    @Override
    public void addAnimeToList(ListBean list, AnimeBean anime) throws ExceptionDao {
        try {
            if (animeExistsInList(list, anime)) {
                throw new ExceptionDao("Anime ID " + anime.getIdAnimeTmdb() + " already exists in list ID " + list.getId() + ".");
            }

            try (CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME), StandardOpenOption.APPEND))) {
                String[] recordAnime = {String.valueOf(list.getId()), String.valueOf(anime.getIdAnimeTmdb())};
                csvWriter.writeNext(recordAnime);
            }
        } catch (IOException e) {
            throw new ExceptionDao("Failed to add anime to list in CSV. I/O error.", e);
        }
    }

    @Override
    public void removeAnimeFromList(ListBean list, AnimeBean anime) throws ExceptionDao {
        try {
            if (!animeExistsInList(list, anime)) {
                throw new ExceptionDao("Anime ID " + anime.getIdAnimeTmdb() + " not found in list ID " + list.getId() + ".");
            }

            List<String[]> allRecords = new ArrayList<>();
            // --- REFACTORED START ---
            // Combined CSVReader and CSVWriter into a single try block
            try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)));
                 CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME)))) { // This will overwrite the file
                String[] recordAnime;
                while ((recordAnime = csvReader.readNext()) != null) {
                    if (recordAnime.length < 2) {
                        continue;
                    }
                    if (!(recordAnime[0].equals(String.valueOf(list.getId())) && recordAnime[1].equals(String.valueOf(anime.getIdAnimeTmdb())))) {
                        allRecords.add(recordAnime);
                    }
                }
                csvWriter.writeAll(allRecords);
            }
            // --- REFACTORED END ---
        } catch (IOException | CsvValidationException e) { // Catch both IOException and CsvValidationException here
            throw new ExceptionDao("Failed to remove anime from list in CSV. I/O or data error.", e);
        }
    }

    @Override
    public List<AnimeBean> getAllAnimeInList(ListBean list) throws ExceptionDao {
        List<AnimeBean> animeList = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] recordAnime;
            while ((recordAnime = csvReader.readNext()) != null) {
                if (recordAnime.length < 2) {
                    continue;
                }
                if (recordAnime[0].equals(String.valueOf(list.getId()))) {
                    int animeId = Integer.parseInt(recordAnime[1]);
                    AnimeBean anime = fetchAnimeById(animeId);
                    if (anime != null) {
                        animeList.add(anime);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            throw new ExceptionDao("Failed to retrieve all animes for list from CSV. Data corruption or I/O error.", e);
        } catch (CsvValidationException e) {
            throw new CsvDaoException("CSV data validation error during getAllAnimeInList.", e);
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

            // --- REFACTORED START ---
            // Combined CSVReader and CSVWriter into a single try block
            try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)));
                 CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME)))) { // This will overwrite the file
                String[] recordAnime;
                while ((recordAnime = csvReader.readNext()) != null) {
                    if (recordAnime.length < 2) {
                        continue;
                    }
                    if (!recordAnime[0].equals(String.valueOf(list.getId()))) {
                        allRecords.add(recordAnime);
                    }
                }
                csvWriter.writeAll(allRecords);
            }
            // --- REFACTORED END ---
        } catch (IOException | CsvValidationException e) { // Catch both IOException and CsvValidationException here
            throw new ExceptionDao("Failed to remove all anime from list in CSV. I/O or data error.", e);
        }
    }

    private boolean animeExistsInList(ListBean list, AnimeBean anime) throws ExceptionDao {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] recordAnime;
            while ((recordAnime = csvReader.readNext()) != null) {
                if (recordAnime.length < 2) {
                    continue;
                }
                if (recordAnime[0].equals(String.valueOf(list.getId())) && recordAnime[1].equals(String.valueOf(anime.getIdAnimeTmdb()))) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new ExceptionDao("Failed to check anime existence in list from CSV. I/O error.", e);
        } catch (CsvValidationException e) {
            throw new CsvDaoException("CSV data validation error during animeExistsInList.", e);
        }
        return false;
    }

    private AnimeBean fetchAnimeById(int id) throws ExceptionDao {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(ANIME_CSV_FILE_NAME)))) {
            String[] recordAnime;
            while ((recordAnime = csvReader.readNext()) != null) {
                if (recordAnime.length < 4) {
                    continue;
                }
                AnimeBean anime = parseAnimeRecord(recordAnime, id);
                if (anime != null) {
                    return anime;
                }
            }
        } catch (IOException e) {
            throw new ExceptionDao("Failed to fetch anime details from main anime CSV file. I/O error.", e);
        } catch (CsvValidationException e) {
            throw new CsvDaoException("CSV data validation error during fetchAnimeById.", e);
        }
        return null;
    }

    private AnimeBean parseAnimeRecord(String[] recordAnime, int targetId) {
        try {
            int currentId = Integer.parseInt(recordAnime[0]);
            if (currentId == targetId) {
                int duration = Integer.parseInt(recordAnime[2]);
                int episodes = Integer.parseInt(recordAnime[1]);
                String title = recordAnime[3];
                return new AnimeBean(currentId, duration, episodes, title);
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Skipping malformed anime record. Record: {0}, Error: {1}", new Object[]{java.util.Arrays.toString(recordAnime), e.getMessage()});
        }
        return null;
    }
}