package no.uio.ifi.in2000.team33.smaafly.data.grib.grib2json;

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;
import com.lexicalscope.jewel.cli.OptionOrder;
import com.lexicalscope.jewel.cli.Unparsed;

import java.io.File;


/**
 * 2013-10-25<p/>
 * <p>
 * Command line options for the grib2json utility. This interface is proxied by the Jewel Cli options parsing library.
 *
 * @author Cameron Beccario
 */
@CommandLineInterface(application = "grib2json", order = OptionOrder.LONGNAME)
public interface Options {


    @Option(longName = "names", shortName = "n", description = "print names of numeric codes")
    boolean getPrintNames();

    @Option(longName = "data", shortName = "d", description = "print GRIB record data")
    boolean getPrintData();

    @Option(longName = "compact", shortName = "c", description = "enable compact Json formatting")
    boolean isCompactFormat();


    @Option(
            longName = "output",
            shortName = "o",
            description = "write output to the specified file (default is stdout)",
            defaultToNull = true)
    File getOutput();

    @Unparsed(name = "FILE", defaultToNull = true)
    File getFile();

    // ============================
    // options to perform filtering

    @Option(
            longName = {"filter.discipline", "fd"},
            description = "select records with this discipline",
            defaultToNull = true)
    Integer getFilterDiscipline();

    @Option(
            longName = {"filter.category", "fc"},
            description = "select records with this numeric category",
            defaultToNull = true)
    Integer getFilterCategory();

    @Option(
            longName = {"filter.parameter", "fp"},
            description = "select records with this numeric parameter, or the string \"wind\" for both u,v components",
            defaultToNull = true)
    String getFilterParameter();

    @Option(
            longName = {"filter.surface", "fs"},
            description = "select records with this numeric surface type",
            defaultToNull = true)
    Integer getFilterSurface();

    @Option(
            longName = {"filter.value", "fv"},
            description = "select records with this numeric surface value",
            defaultToNull = true)
    Double getFilterValue();

    @Option(
            longName = "recipe",
            shortName = "r",
            description = "a file containing a batch of filter options: fd, fc, fp, fs, fv, and o",
            defaultToNull = true)
    File getRecipe();
}