package io.openright.parental.server;

import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.sql.DataSource;
import java.io.File;

public class ParentalBenefitsTestConfig extends ParentalBenefitsConfigFile {

    private static ParentalBenefitsTestConfig parentalBenefitsTestConfig = new ParentalBenefitsTestConfig();

    public ParentalBenefitsTestConfig() {
        super(new File("test-parental-benefits.properties"));
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    protected DataSource createDataSource() {
        return createTestDataSource("parental");
    }

    public static ParentalBenefitsTestConfig instance() {
        return parentalBenefitsTestConfig;
    }

}
