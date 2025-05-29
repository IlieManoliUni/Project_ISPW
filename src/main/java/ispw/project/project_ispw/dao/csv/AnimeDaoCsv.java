package ispw.project.project_ispw.dao.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.dao.AnimeDao;
import ispw.project.project_ispw.exception.ExceptionDao;
import ispw.project.project_ispw.exception.CsvDaoException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class AnimeDaoCsv implements AnimeDao {

    private static final String CSV_FILE_NAME;
    private final HashMap<Integer, AnimeBean> localCache;

    static {
        CSV_FILE_NAME = loadCsvFileName();
        try {
            if (!Files.exists(Paths.get(CSV_FILE_NAME))) {
                Files.createFile(Paths.get(CSV_FILE_NAME));
            }
        } catch (IOException e) {

            throw new CsvDaoException("Initialization failed: Could not create CSV file at " + CSV_FILE_NAME, e);
        }
    }

    public AnimeDaoCsv() {
        this.localCache = new HashMap<>();
    }

    private static String loadCsvFileName() {
        Properties properties = new Properties();
        try (InputStream input = AnimeDaoCsv.class.getClassLoader().getResourceAsStream("csv.properties")) {
            if (input == null) {

                throw new IllegalStateException("csv.properties file not found in resources folder. Cannot initialize CSV DAO.");
            }
            properties.load(input);
            String filename = properties.getProperty("anime.csv.filename");
            if (filename == null || filename.trim().isEmpty()) {
                filename = "anime.csv";
            }
            return filename;
        } catch (IOException e) {
            throw new CsvDaoException("Initialization failed: Error loading csv.properties.", e);
        }
    }

    @Override
    public AnimeBean retrieveById(int id) throws ExceptionDao {
        synchronized (localCache) {
            if (localCache.containsKey(id)) {
                return localCache.get(id);
            }
        }

        AnimeBean anime = null;
        try {
            anime = retrieveByIdFromFile(id);
        } catch (IOException | NumberFormatException e) {
            throw new ExceptionDao("Failed to retrieve anime from CSV for ID: " + id + ". Data corruption or I/O error.", e);
        } catch (CsvDaoException e) {
            throw new ExceptionDao("CSV data validation error while retrieving anime for ID: " + id, e);
        }

        if (anime != null) {
            synchronized (localCache) {
                localCache.put(id, anime);
            }
        }
        return anime;
    }

    private static AnimeBean retrieveByIdFromFile(int id) throws IOException, NumberFormatException {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] recordAnime;
            while ((recordAnime = csvReader.readNext()) != null) {
                if (recordAnime.length < 4) {
                    continue;
                }
                int currentId = Integer.parseInt(recordAnime[0]);
                if (currentId == id) {
                    return new AnimeBean(currentId, Integer.parseInt(recordAnime[2]), Integer.parseInt(recordAnime[1]), recordAnime[3]);
                }
            }
        } catch (CsvValidationException e) {
            throw new CsvDaoException("CSV data validation error during retrieval by ID.", e);
        }
        return null;
    }

    @Override
    public void saveAnime(AnimeBean anime) throws ExceptionDao {
        int animeId = anime.getIdAnimeTmdb();

        synchronized (localCache) {
            if (localCache.containsKey(animeId)) {
                throw new ExceptionDao("Duplicated Anime ID already in cache: " + animeId);
            }
        }

        AnimeBean existingAnime = null;
        try {
            existingAnime = retrieveByIdFromFile(animeId);
        } catch (IOException | NumberFormatException e) {
            throw new ExceptionDao("Failed to check existing anime for ID: " + animeId + ". Data corruption or I/O error.", e);
        } catch (CsvDaoException e) {
            throw new ExceptionDao("CSV data validation error while checking existing anime for ID: " + animeId, e);
        }

        if (existingAnime != null) {

            throw new ExceptionDao("Duplicated Anime ID already exists in CSV file: " + animeId);
        }

        try {
            saveAnimeToFile(anime);
        } catch (IOException e) {
            throw new ExceptionDao("Failed to save anime to CSV for ID: " + animeId + ". I/O error.", e);
        }

        synchronized (localCache) {
            localCache.put(animeId, anime);
        }
    }

    private static void saveAnimeToFile(AnimeBean anime) throws IOException {

        try (CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME), java.nio.file.StandardOpenOption.APPEND))) {
            String[] recordAnime = {
                    String.valueOf(anime.getIdAnimeTmdb()),
                    String.valueOf(anime.getEpisodes()),
                    String.valueOf(anime.getDuration()),
                    anime.getTitle()
            };
            csvWriter.writeNext(recordAnime);
        }
    }

    @Override
    public List<AnimeBean> retrieveAllAnime() throws ExceptionDao {
        List<AnimeBean> animeList;

        try {
            animeList = retrieveAllAnimeFromFile();
        } catch (IOException | NumberFormatException e) {
            throw new ExceptionDao("Failed to retrieve all animes from CSV. Data corruption or I/O error.", e);
        } catch (CsvDaoException e) {
            throw new ExceptionDao("CSV data validation error while retrieving all animes.", e);
        }

        synchronized (localCache) {
            localCache.clear();
            for (AnimeBean anime : animeList) {
                localCache.put(anime.getIdAnimeTmdb(), anime);
            }
        }

        return Collections.unmodifiableList(animeList);
    }

    private static List<AnimeBean> retrieveAllAnimeFromFile() throws IOException, NumberFormatException {
        List<AnimeBean> animeList = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] recordAnime;
            while ((recordAnime = csvReader.readNext()) != null) {
                if (recordAnime.length < 4) {
                    continue;
                }
                animeList.add(new AnimeBean(Integer.parseInt(recordAnime[0]), Integer.parseInt(recordAnime[2]), Integer.parseInt(recordAnime[1]), recordAnime[3]));
            }
        } catch (CsvValidationException e) {
            throw new CsvDaoException("CSV data validation error during retrieval of all animes.", e);
        }
        return animeList;
    }
}