package ispw.project.project_ispw.dao.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.dao.ListTvSeries;
import ispw.project.project_ispw.exception.CsvDaoException;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListTvSeriesDaoCsv implements ListTvSeries {
    private static final Logger LOGGER = Logger.getLogger(ListTvSeriesDaoCsv.class.getName());

    private static final String CSV_FILE_NAME;
    private static final String TVSERIES_CSV_FILE_NAME;

    static {
        Properties properties = new Properties();
        String listTvSeriesFileName = "list_tvseries.csv";
        String tvSeriesFileName = "tvseries.csv";

        try (InputStream input = ListTvSeriesDaoCsv.class.getClassLoader().getResourceAsStream("csv.properties")) {
            if (input != null) {
                properties.load(input);
                listTvSeriesFileName = properties.getProperty("list_tvseries.csv.filename", listTvSeriesFileName);
                tvSeriesFileName = properties.getProperty("tvseries.csv.filename", tvSeriesFileName);
            } else {
                LOGGER.log(Level.WARNING, "csv.properties file not found. Using default CSV filenames: list_tvseries.csv=''{0}'', tvseries.csv=''{1}''",
                        new Object[]{listTvSeriesFileName, tvSeriesFileName});
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load csv.properties. Using default CSV filenames: list_tvseries.csv=''{0}'', tvseries.csv=''{1}''. Error: {2}",
                    new Object[]{listTvSeriesFileName, tvSeriesFileName, e.getMessage()});
        }

        CSV_FILE_NAME = listTvSeriesFileName;
        TVSERIES_CSV_FILE_NAME = tvSeriesFileName;

        try {
            if (!Files.exists(Paths.get(CSV_FILE_NAME))) {
                Files.createFile(Paths.get(CSV_FILE_NAME));
            }
            if (!Files.exists(Paths.get(TVSERIES_CSV_FILE_NAME))) {
                Files.createFile(Paths.get(TVSERIES_CSV_FILE_NAME));
            }
        } catch (IOException e) {
            throw new CsvDaoException("Initialization failed: Could not create CSV files.", e);
        }
    }

    public ListTvSeriesDaoCsv() {
        //Empty constructor
    }

    @Override
    public void addTvSeriesToList(ListBean list, TvSeriesBean tvSeries) throws ExceptionDao {
        try {
            if (tvSeriesExistsInList(list, tvSeries)) {
                throw new ExceptionDao("TV Series ID " + tvSeries.getIdTvSeriesTmdb() + " already exists in list ID " + list.getId() + ".");
            }

            try (CSVWriter writer = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME), StandardOpenOption.APPEND))) {
                String[] recordListTvSeries = {
                        String.valueOf(list.getId()),
                        String.valueOf(tvSeries.getIdTvSeriesTmdb())
                };
                writer.writeNext(recordListTvSeries);
            }
        } catch (IOException e) {
            throw new ExceptionDao("Failed to add TV Series to list in CSV. I/O or data error.", e);
        }
    }

    @Override
    public void removeTvSeriesFromList(ListBean list, TvSeriesBean tvSeries) throws ExceptionDao {
        Path originalPath = Paths.get(CSV_FILE_NAME);
        Path tempPath = Paths.get(CSV_FILE_NAME + ".tmp");
        List<String[]> allRecords = new ArrayList<>();

        try (CSVReader reader = new CSVReader(Files.newBufferedReader(originalPath));
             CSVWriter writer = new CSVWriter(Files.newBufferedWriter(tempPath))) {

            if (!tvSeriesExistsInList(list, tvSeries)) {
                throw new ExceptionDao("TV Series ID " + tvSeries.getIdTvSeriesTmdb() + " not found in list ID " + list.getId() + ".");
            }

            String[] recordListTvSeries;
            while ((recordListTvSeries = reader.readNext()) != null) {
                if (recordListTvSeries.length < 2) {
                    continue;
                }
                processRecordForTvSeriesRemoval(recordListTvSeries, list.getId(), tvSeries.getIdTvSeriesTmdb(), allRecords);
            }
            writer.writeAll(allRecords);
            Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new ExceptionDao("Failed to remove TV Series from list in CSV. I/O error.", e);
        } catch (CsvValidationException e) {
            throw new CsvDaoException("CSV data validation error during removeTvSeriesFromList.", e);
        }
    }

    private void processRecordForTvSeriesRemoval(String[] recordListTvSeries, int listId, int tvSeriesIdToDelete, List<String[]> allRecords) {
        try {
            if (!(Integer.parseInt(recordListTvSeries[0]) == listId && Integer.parseInt(recordListTvSeries[1]) == tvSeriesIdToDelete)) {
                allRecords.add(recordListTvSeries);
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Skipping removal for malformed list-TV series record in CSV due to invalid ID format. Record will be preserved. Record: {0}, Error: {1}",
                    new Object[]{java.util.Arrays.toString(recordListTvSeries), e.getMessage()});
            allRecords.add(recordListTvSeries);
        }
    }

    @Override
    public List<TvSeriesBean> getAllTvSeriesInList(ListBean list) throws ExceptionDao {
        List<TvSeriesBean> tvSeriesList = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] recordListTvSeries;
            while ((recordListTvSeries = csvReader.readNext()) != null) {
                if (recordListTvSeries.length < 2) {
                    continue;
                }
                if (recordListTvSeries[0].equals(String.valueOf(list.getId()))) {
                    TvSeriesBean tvSeries = processListTvSeriesRecordAndFetchTvSeries(recordListTvSeries, list.getId());
                    if (tvSeries != null) {
                        tvSeriesList.add(tvSeries);
                    }
                }
            }
        } catch (IOException e) {
            throw new ExceptionDao("Failed to retrieve all TV Series for list from CSV. I/O error.", e);
        } catch (CsvValidationException e) {
            throw new CsvDaoException("CSV data validation error during getAllTvSeriesInList.", e);
        }

        return tvSeriesList;
    }

    private TvSeriesBean processListTvSeriesRecordAndFetchTvSeries(String[] recordListTvSeries, int listId) throws ExceptionDao {
        try {
            if (Integer.parseInt(recordListTvSeries[0]) == listId) {
                int tvSeriesId = Integer.parseInt(recordListTvSeries[1]);
                TvSeriesBean tvSeries = fetchTvSeriesById(tvSeriesId);

                if (tvSeries != null) {
                    return tvSeries;
                } else {
                    LOGGER.log(Level.WARNING, "TV Series with ID {0} found in list ID {1}, but details not found in tvseries.csv. Skipping this entry. Record: {2}",
                            new Object[]{tvSeriesId, listId, java.util.Arrays.toString(recordListTvSeries)});
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Skipping malformed list-TV series record in CSV due to invalid TV Series ID format. Record: {0}, Error: {1}",
                    new Object[]{java.util.Arrays.toString(recordListTvSeries), e.getMessage()});
        }
        return null;
    }

    @Override
    public void removeAllTvSeriesFromList(ListBean list) throws ExceptionDao {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }

        Path originalPath = Paths.get(CSV_FILE_NAME);
        Path tempPath = Paths.get(CSV_FILE_NAME + ".tmp");
        List<String[]> allRecordsToKeep = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(originalPath));
             CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(tempPath))) {

            String[] recordListTvSeries;
            while ((recordListTvSeries = csvReader.readNext()) != null) {
                if (recordListTvSeries.length < 2) {
                    continue;
                }
                if (!recordListTvSeries[0].equals(String.valueOf(list.getId()))) {
                    allRecordsToKeep.add(recordListTvSeries);
                }
            }
            csvWriter.writeAll(allRecordsToKeep);
            Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new ExceptionDao("Failed to remove all TV series from list in CSV. I/O error.", e);
        } catch (CsvValidationException e) {
            throw new CsvDaoException("CSV data validation error during removeAllTvSeriesFromList.", e);
        }
    }

    private boolean checkTvSeriesRecordForExistence(String[] recordListTvSeries, ListBean list, TvSeriesBean tvSeries) {
        try {
            return Integer.parseInt(recordListTvSeries[0]) == list.getId() &&
                    Integer.parseInt(recordListTvSeries[1]) == tvSeries.getIdTvSeriesTmdb();
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Skipping list-TV series record with invalid ID format during existence check. Expected numeric IDs, but found malformed data. Record: {0}, Error: {1}",
                    new Object[]{java.util.Arrays.toString(recordListTvSeries), e.getMessage()});
            return false;
        }
    }

    private boolean tvSeriesExistsInList(ListBean list, TvSeriesBean tvSeries) throws ExceptionDao {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] recordListTvSeries;
            while ((recordListTvSeries = csvReader.readNext()) != null) {
                if (recordListTvSeries.length < 2) {
                    continue;
                }
                if (checkTvSeriesRecordForExistence(recordListTvSeries, list, tvSeries)) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new ExceptionDao("Failed to check TV Series existence in list from CSV. I/O or data error.", e);
        } catch (CsvValidationException e) {
            throw new CsvDaoException("CSV data validation error during tvSeriesExistsInList.", e);
        }
        return false;
    }

    private TvSeriesBean parseAndMatchTvSeriesRecord(String[] recordTvSeries, int targetId) {
        try {
            int currentId = Integer.parseInt(recordTvSeries[0]);
            if (currentId == targetId) {
                int episodeRuntime = Integer.parseInt(recordTvSeries[1]);
                int numberOfEpisodes = Integer.parseInt(recordTvSeries[2]);
                String name = recordTvSeries[3];
                return new TvSeriesBean(episodeRuntime, currentId, numberOfEpisodes, name);
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Skipping malformed TV series record in CSV. Expected numeric ID, episode runtime, or number of episodes, but found invalid data. Record: {0}, Error: {1}",
                    new Object[]{java.util.Arrays.toString(recordTvSeries), e.getMessage()});
        }
        return null;
    }

    private TvSeriesBean fetchTvSeriesById(int id) throws ExceptionDao {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(TVSERIES_CSV_FILE_NAME)))) {
            String[] recordTvSeries;
            while ((recordTvSeries = csvReader.readNext()) != null) {
                if (recordTvSeries.length < 4) {
                    continue;
                }
                TvSeriesBean tvSeries = parseAndMatchTvSeriesRecord(recordTvSeries, id);
                if (tvSeries != null) {
                    return tvSeries;
                }
            }
        } catch (IOException e) {
            throw new ExceptionDao("Failed to fetch TV Series details from main TV Series CSV file. I/O or data error.", e);
        } catch (CsvValidationException e) {
            throw new CsvDaoException("CSV data validation error during fetchTvSeriesById.", e);
        }
        return null;
    }
}