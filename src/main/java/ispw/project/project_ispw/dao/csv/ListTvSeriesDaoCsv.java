package ispw.project.project_ispw.dao.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.dao.ListTvSeries;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ListTvSeriesDaoCsv implements ListTvSeries {

    private static final String CSV_FILE_NAME;
    private static final String TVSERIES_CSV_FILE_NAME; // To get TV series details

    // Static block to load CSV file names and ensure files exist
    static {
        Properties properties = new Properties();
        String listTvSeriesFileName = "list_tvseries.csv"; // Default
        String tvSeriesFileName = "tvseries.csv"; // Default for fetching TV series details

        try (InputStream input = ListTvSeriesDaoCsv.class.getClassLoader().getResourceAsStream("csv.properties")) {
            if (input != null) {
                properties.load(input);
                listTvSeriesFileName = properties.getProperty("list_tvseries.csv.filename", listTvSeriesFileName);
                tvSeriesFileName = properties.getProperty("tvseries.csv.filename", tvSeriesFileName);
            } else {
                // csv.properties file not found, using default CSV filenames.
            }
        } catch (IOException e) {
            // Error loading properties, using default filenames.
        }

        CSV_FILE_NAME = listTvSeriesFileName;
        TVSERIES_CSV_FILE_NAME = tvSeriesFileName;

        // Ensure both files exist
        try {
            if (!Files.exists(Paths.get(CSV_FILE_NAME))) {
                Files.createFile(Paths.get(CSV_FILE_NAME));
            }
            if (!Files.exists(Paths.get(TVSERIES_CSV_FILE_NAME))) {
                Files.createFile(Paths.get(TVSERIES_CSV_FILE_NAME));
            }
        } catch (IOException e) {
            throw new RuntimeException("Initialization failed: Could not create CSV files.", e);
        }
    }

    public ListTvSeriesDaoCsv() {
        // Constructor no longer needs to create the file; the static block handles it.
    }

    @Override
    public void addTvSeriesToList(ListBean list, TvSeriesBean tvSeries) throws ExceptionDao {
        try {
            // Check if the TV Series already exists in the list
            if (tvSeriesExistsInList(list, tvSeries)) {
                throw new ExceptionDao("TV Series ID " + tvSeries.getIdTvSeriesTmdb() + " already exists in list ID " + list.getId() + ".");
            }

            // Add the TV Series to the CSV file
            try (CSVWriter writer = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME), StandardOpenOption.APPEND))) {
                String[] record = {
                        String.valueOf(list.getId()),
                        String.valueOf(tvSeries.getIdTvSeriesTmdb())
                };
                writer.writeNext(record);
            }
        } catch (IOException e) { // Added CsvValidationException
            throw new ExceptionDao("Failed to add TV Series to list in CSV. I/O or data error.", e);
        }
    }

    @Override
    public void removeTvSeriesFromList(ListBean list, TvSeriesBean tvSeries) throws ExceptionDao {
        try {
            // Check if the TV Series exists in the list
            if (!tvSeriesExistsInList(list, tvSeries)) {
                throw new ExceptionDao("TV Series ID " + tvSeries.getIdTvSeriesTmdb() + " not found in list ID " + list.getId() + ".");
            }

            java.nio.file.Path originalPath = Paths.get(CSV_FILE_NAME);
            java.nio.file.Path tempPath = Paths.get(CSV_FILE_NAME + ".tmp");
            List<String[]> allRecords = new ArrayList<>();
            String[] record = null; // Declare record outside try-catch to be accessible in catch block

            try (CSVReader reader = new CSVReader(Files.newBufferedReader(originalPath))) {
                while ((record = reader.readNext()) != null) {
                    if (record.length < 2) {
                        continue; // Skip malformed records
                    }
                    try {
                        if (!(Integer.parseInt(record[0]) == list.getId() && Integer.parseInt(record[1]) == tvSeries.getIdTvSeriesTmdb())) {
                            allRecords.add(record);
                        }
                    } catch (NumberFormatException e) {
                        // Decide whether to keep or discard malformed records. For removal, safer to keep.
                        allRecords.add(record);
                    }
                }
            } catch (CsvValidationException e) { // Added CsvValidationException
                throw new IOException("CSV validation error.", e);
            }

            try (CSVWriter writer = new CSVWriter(Files.newBufferedWriter(tempPath))) {
                writer.writeAll(allRecords);
            }

            Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) { // Catch IOException for file operations
            throw new ExceptionDao("Failed to remove TV Series from list in CSV. I/O or data error.", e);
        }
    }

    @Override
    public List<TvSeriesBean> getAllTvSeriesInList(ListBean list) throws ExceptionDao {
        List<TvSeriesBean> tvSeriesList = new ArrayList<>();
        String[] record = null; // Declare record outside try-catch for logging

        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 2) {
                    continue; // Skip malformed records
                }
                try {
                    if (Integer.parseInt(record[0]) == list.getId()) {
                        int tvSeriesId = Integer.parseInt(record[1]);
                        TvSeriesBean tvSeries = fetchTvSeriesById(tvSeriesId);

                        if (tvSeries != null) {
                            tvSeriesList.add(tvSeries);
                        } else {
                            // TV Series with ID found in list but not in tvseries.csv.
                        }
                    }
                } catch (NumberFormatException e) {
                    // Skipping CSV record with invalid ID format.
                }
            }
        } catch (IOException | CsvValidationException e) { // Added CsvValidationException
            throw new ExceptionDao("Failed to retrieve all TV Series for list from CSV. Data corruption or I/O error.", e);
        }

        if (tvSeriesList.isEmpty()) {
            throw new ExceptionDao("No TV Series Found in the List (ID: " + list.getId() + ").");
        }

        return tvSeriesList;
    }

    @Override
    public void removeAllTvSeriesFromList(ListBean list) throws ExceptionDao {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }

        java.nio.file.Path originalPath = Paths.get(CSV_FILE_NAME);
        java.nio.file.Path tempPath = Paths.get(CSV_FILE_NAME + ".tmp");
        List<String[]> allRecordsToKeep = new ArrayList<>();
        boolean listHadEntries = false; // Flag to check if the list actually contained entries

        try {
            // Read all records, excluding those for the specified list ID
            try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(originalPath))) {
                String[] record;
                while ((record = csvReader.readNext()) != null) {
                    if (record.length < 2) {
                        continue; // Skip malformed records
                    }
                    // Keep records that do NOT match the list ID
                    if (!record[0].equals(String.valueOf(list.getId()))) {
                        allRecordsToKeep.add(record);
                    } else {
                        listHadEntries = true; // Mark that we found entries for this list ID
                    }
                }
            }

            // Write the filtered records back to a temporary file
            try (CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(tempPath))) {
                csvWriter.writeAll(allRecordsToKeep);
            }

            // Atomically replace the original file with the temporary one
            Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING);

            if (!listHadEntries) {
                // If the list ID was not found in the CSV, throw an exception
                throw new ExceptionDao("List with ID " + list.getId() + " not found or already empty.");
            }

        } catch (IOException | CsvValidationException e) {
            throw new ExceptionDao("Failed to remove all TV series from list in CSV. I/O or data error.", e);
        }
    }


    // Helper method to check if a TV Series is already in the List
    private boolean tvSeriesExistsInList(ListBean list, TvSeriesBean tvSeries) throws ExceptionDao {
        String[] record = null; // Declare record outside try-catch for logging
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 2) {
                    continue; // Skip malformed records
                }
                try {
                    if (Integer.parseInt(record[0]) == list.getId() && Integer.parseInt(record[1]) == tvSeries.getIdTvSeriesTmdb()) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    // Skipping CSV record with invalid ID format.
                }
            }
        } catch (IOException | CsvValidationException e) { // Added CsvValidationException
            throw new ExceptionDao("Failed to check TV Series existence in list from CSV. I/O or data error.", e);
        }
        return false;
    }

    // Helper method to fetch a TV series by its ID from the "tvseries.csv" file
    private TvSeriesBean fetchTvSeriesById(int id) throws ExceptionDao {
        String[] record = null; // Declare record outside try-catch for logging
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(TVSERIES_CSV_FILE_NAME)))) {
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 4) { // Assuming ID, episodeRuntime, numberOfEpisodes, name
                    continue; // Skip malformed TV Series CSV records
                }
                try {
                    if (Integer.parseInt(record[0]) == id) {
                        // Assuming TvSeriesBean constructor: public TvSeriesBean(int episodeRuntime, int idTvSeriesTmdb, int numberOfEpisodes, String name)
                        // Make sure the indices match your TVSeries CSV file structure and TvSeriesBean constructor
                        return new TvSeriesBean(Integer.parseInt(record[1]), Integer.parseInt(record[0]), Integer.parseInt(record[2]), record[3]);
                    }
                } catch (NumberFormatException e) {
                    // Skipping CSV record with invalid number format.
                }
            }
        } catch (IOException | CsvValidationException e) { // Added CsvValidationException
            throw new ExceptionDao("Failed to fetch TV Series details from main TV Series CSV file. I/O or data error.", e);
        }
        return null; // TV Series not found
    }
}