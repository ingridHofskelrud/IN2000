package no.uio.ifi.in2000.team33.smaafly.data.grib;

import com.lexicalscope.jewel.cli.CliFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import no.uio.ifi.in2000.team33.smaafly.data.grib.grib2json.Grib2Json;
import no.uio.ifi.in2000.team33.smaafly.data.grib.grib2json.Options;

public class WriteJson {
    /**
     * Convert grib2 file to json file
     *
     * @param inFile  File to be converted
     * @param outFile File for data to be written
     * @throws IOException At failure to read/write
     */
    public static void write(File inFile, File outFile) throws IOException {
        Options options = CliFactory.parseArguments(Options.class, "--data", "--names");
        List<Options> optionGroups = Collections.singletonList(options);

        Grib2Json g2j = new Grib2Json(inFile, optionGroups, outFile);
        g2j.write();
    }
}
