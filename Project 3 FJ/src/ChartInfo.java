public class ChartInfo {
    private String dbURL;
    private String dbUsername;
    private String dbPassword;
    private String dbQuery;
    private String chartDesc;
    private String xLabel;
    private String yLabel;
    private String rangeSelector;

    // PieChart

    /**
     *
     * @param dbURL
     * @param dbUsername
     * @param dbPassword
     * @param dbQuery
     * @param rangeSelector
     */
    public ChartInfo(String dbURL, String dbUsername, String dbPassword, String dbQuery, String rangeSelector) {
        this.dbURL = dbURL;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.dbQuery = dbQuery;
        this.rangeSelector = rangeSelector;
    }

    // AreaChart

    /**
     *
     * @param dbURL
     * @param dbUsername
     * @param dbPassword
     * @param dbQuery
     * @param chartDesc
     * @param xLabel
     * @param yLabel
     * @param rangeSelector
     */
    public ChartInfo(String dbURL, String dbUsername, String dbPassword, String dbQuery, String chartDesc, String xLabel, String yLabel, String rangeSelector) {
        this.dbURL = dbURL;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.dbQuery = dbQuery;
        this.chartDesc = chartDesc;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.rangeSelector = rangeSelector;
    }

    public String getDbURL() {
        return dbURL;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbQuery() {
        return dbQuery;
    }

    public String getChartDesc() {
        return chartDesc;
    }

    public String getxLabel() {
        return xLabel;
    }

    public String getyLabel() {
        return yLabel;
    }

    public String getRangeSelector() {
        return rangeSelector;
    }

    public void setRangeSelector(String rangeSelector) {
        this.rangeSelector = rangeSelector;
    }
}