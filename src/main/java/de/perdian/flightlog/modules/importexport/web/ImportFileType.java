package de.perdian.flightlog.modules.importexport.web;

import java.io.Reader;
import java.util.function.Supplier;

import de.perdian.flightlog.modules.importexport.data.DataLoader;
import de.perdian.flightlog.modules.importexport.data.impl.JsonDataLoader;
import de.perdian.flightlog.modules.importexport.data.impl.OpenflightsCsvDataLoader;
import de.perdian.flightlog.modules.importexport.data.impl.XmlDataLoader;

public enum ImportFileType {

    XML(XmlDataLoader::new),
    JSON(JsonDataLoader::new),
    OPENFLIGHTSCSV(OpenflightsCsvDataLoader::new);

    private Supplier<DataLoader<Reader>> dataLoaderSupplier = null;

    private ImportFileType(Supplier<DataLoader<Reader>> dataLoaderSupplier) {
        this.setDataLoaderSupplier(dataLoaderSupplier);
    }

    Supplier<DataLoader<Reader>> getDataLoaderSupplier() {
        return this.dataLoaderSupplier;
    }
    private void setDataLoaderSupplier(Supplier<DataLoader<Reader>> dataLoaderSupplier) {
        this.dataLoaderSupplier = dataLoaderSupplier;
    }


}
