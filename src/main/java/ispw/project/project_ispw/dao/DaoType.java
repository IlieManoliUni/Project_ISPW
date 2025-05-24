package ispw.project.project_ispw.dao;

/**
 * Defines the types of Data Access Object (DAO) implementations available
 * for persistence in the full version of the application.
 */
public enum DaoType {
    JDBC,
    CSV,
    IN_MEMORY // Can be used for DemoState, but explicitly not for FullModeState
}