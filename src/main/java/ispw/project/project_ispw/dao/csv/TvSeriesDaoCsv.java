package ispw.project.project_ispw.dao.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.dao.TvSeriesDao;
import ispw.project.project_ispw.exception.CsvDaoException;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TvSeriesDaoCsv implements TvSeriesDao {

    private static final Logger LOGGER = Logger.getLogger(TvSeriesDaoCsv.class.getName());

    private static final String CSV_FILE_NAME;

    private final HashMap<Integer, TvSeriesBean> localCache;

    static {
        Properties properties = new Properties();
        String fileName = "tvseries.csv";

        try (InputStream input = TvSeriesDaoCsv.class.getClassLoader().getResourceAsStream("csv.properties")) {
            if (input != null) {
                properties.load(input);
                fileName = properties.getProperty("tvseries.csv.filename", fileName);
            } else {
                LOGGER.log(Level.WARNING, "csv.properties file not found. Using default filename: {0}", fileName);
            }
        } catch (IOException e) {
            throw new CsvDaoException("Initialization failed: Error loading csv.properties.", e);
        }
        CSV_FILE_NAME = fileName;

        try {
            if (!Files.exists(Paths.get(CSV_FILE_NAME))) {
                Files.createFile(Paths.get(CSV_FILE_NAME));
            }
        } catch (IOException e) {
            throw new CsvDaoException("Initialization failed: Could not create CSV file for TV series.", e);
        }
    }

    public TvSeriesDaoCsv() {
        this.localCache = new HashMap<>();
    }

    @Override
    public TvSeriesBean retrieveById(int id) throws ExceptionDao {
        synchronized (localCache) {
            if (localCache.containsKey(id)) {
                return localCache.get(id);
            }
        }

        TvSeriesBean tvSeries = null;
        try {
            tvSeries = retrieveByIdFromFile(id);
        } catch (IOException | NumberFormatException e) {
            throw new ExceptionDao("Failed to retrieve TV Series from CSV for ID: " + id + ". Data corruption or I/O error.", e);
        } catch (CsvValidationException e) {
            throw new CsvDaoException("CSV data validation error while retrieving TV Series for ID: " + id, e);
        }

        if (tvSeries != null) {
            synchronized (localCache) {
                localCache.put(id, tvSeries);
            }
        }
        return tvSeries;
    }

    private TvSeriesBean retrieveByIdFromFile(int id) throws IOException, CsvValidationException {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] recordTvSeries;
            while ((recordTvSeries = csvReader.readNext()) != null) {
                if (recordTvSeries.length < 4) {
                    continue;
                }
                try {
                    int currentId = Integer.parseInt(recordTvSeries[0]);
                    if (currentId == id) {
                        return new TvSeriesBean(currentId, Integer.parseInt(recordTvSeries[1]), Integer.parseInt(recordTvSeries[2]), recordTvSeries[3]);
                    }
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Skipping malformed TV Series record during retrieveByIdFromFile due to invalid ID/runtime/episodes format. Record: {0}, Error: {1}",
                            new Object[]{java.util.Arrays.toString(recordTvSeries), e.getMessage()});
                }
            }
        }
        return null;
    }

    @Override
    public boolean saveTvSeries(TvSeriesBean tvSeries) throws ExceptionDao {
        int tvSeriesId = tvSeries.getIdTvSeriesTmdb();

        synchronized (localCache) {
            if (localCache.containsKey(tvSeriesId)) {
                throw new ExceptionDao("Duplicated TV Series ID already in cache: " + tvSeriesId);
            }
        }

        TvSeriesBean existingTvSeries = null;
        try {
            existingTvSeries = retrieveByIdFromFile(tvSeriesId);
        } catch (IOException | CsvValidationException e) {
            throw new ExceptionDao("Failed to check existing TV Series for ID: " + tvSeriesId + ". I/O error.", e);
        } catch (NumberFormatException e) {
            throw new ExceptionDao("Data corruption while checking existing TV Series for ID: " + tvSeriesId + ". Invalid number format.", e);
        }

        if (existingTvSeries != null) {
            throw new ExceptionDao("Duplicated TV Series ID already exists in CSV file: " + tvSeriesId);
        }

        try {
            saveTvSeriesToFile(tvSeries);
        } catch (IOException e) {
            throw new ExceptionDao("Failed to save TV Series to CSV for ID: " + tvSeriesId + ". I/O error.", e);
        }

        synchronized (localCache) {
            localCache.put(tvSeriesId, tvSeries);
        }

        return true;
    }

    private void saveTvSeriesToFile(TvSeriesBean tvSeries) throws IOException {
        try (CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME), StandardOpenOption.APPEND))) {
            String[] recordTvSeries = {
                    String.valueOf(tvSeries.getIdTvSeriesTmdb()),
                    String.valueOf(tvSeries.getEpisodeRuntime()),
                    String.valueOf(tvSeries.getNumberOfEpisodes()),
                    tvSeries.getName()
            };
            csvWriter.writeNext(recordTvSeries);
        }
    }

    @Override
    public List<TvSeriesBean> retrieveAllTvSeries() throws ExceptionDao {
        List<TvSeriesBean> tvSeriesList = new ArrayList<>();
        try {
            tvSeriesList = retrieveAllTvSeriesFromFile();
        } catch (IOException e) {
            throw new ExceptionDao("Failed to retrieve all TV Series from CSV. I/O error.", e);
        } catch (CsvValidationException e) {
            throw new CsvDaoException("CSV data validation error while retrieving all TV Series.", e);
        } catch (NumberFormatException e) {
            throw new ExceptionDao("Data corruption while retrieving all TV Series. Invalid number format.", e);
        }

        synchronized (localCache) {
            localCache.clear();
            for (TvSeriesBean tvSeries : tvSeriesList) {
                localCache.put(tvSeries.getIdTvSeriesTmdb(), tvSeries);
            }
        }

        return Collections.unmodifiableList(tvSeriesList);
    }

    private List<TvSeriesBean> retrieveAllTvSeriesFromFile() throws IOException, CsvValidationException {
        List<TvSeriesBean> tvSeriesList = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] recordTvSeries;
            while ((recordTvSeries = csvReader.readNext()) != null) {
                if (recordTvSeries.length < 4) {
                    continue;
                }
                try {
                    tvSeriesList.add(new TvSeriesBean(Integer.parseInt(recordTvSeries[0]), Integer.parseInt(recordTvSeries[1]), Integer.parseInt(recordTvSeries[2]), recordTvSeries[3]));
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Skipping malformed TV Series record during retrieveAllTvSeriesFromFile due to invalid ID/runtime/episodes format. Record: {0}, Error: {1}",
                            new Object[]{java.util.Arrays.toString(recordTvSeries), e.getMessage()});
                }
            }
        }
        return tvSeriesList;
    }
}