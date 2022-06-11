import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;

import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;

public class ImageMaker {
    static final int VERSION = 1;
    public static void main(String[] args) {

        boolean showHelp = false;
        boolean doDither = false;
        boolean doResize = false;
        boolean savePostImage = false;
        boolean saveRawFile = false;
        boolean saveInterlacedFiles = false;
        boolean doAllModes = false;
        boolean saveComFile = false;
        String comFilePath = "";
        String interlacedFilesPath = "";
        String rawFilePath = "";
        String postImagePath = "";
        CommandLine commandLine;
        Option option_postImageFile = Option.builder("post")
                .required(false)
                .desc("Path to post-processed image")
                .hasArg(true)
                .build();
        Option option_doDither = Option.builder("d")
                .required(false)
                .desc("Do dithering")
                .longOpt("dither")
                .hasArg(false)
                .build();
        Option option_outputFile = Option.builder("raw")
                .required(false)
                .desc("Path to raw binary file")
                .hasArg(true)
                .build();
        Option option_interlacedFiles = Option.builder("interlaced")
                .required(false)
                .desc("Path to raw interlaced binary files")
                .hasArg(true)
                .build();
        Option option_doAll = Option.builder("all")
                .required(false)
                .desc("Generate this image in every mode")
                .hasArg(false)
                .build();
        Option option_outputComFile = Option.builder("com")
                .required(false)
                .desc("Generate com file")
                .hasArg(true)
                .build();
        Option option_resize = Option.builder("resize")
                .required(false)
                .desc("Resize the images to native CGA resolution")
                .hasArg(false)
                .build();
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();

        options.addOption(option_postImageFile);
        options.addOption(option_doDither);
        options.addOption(option_outputFile);
        options.addOption(option_interlacedFiles);
        options.addOption(option_doAll);
        options.addOption(option_outputComFile);
        options.addOption(option_resize);

        System.out.println("CGA Image Generator version " + VERSION);

        try {
            commandLine = parser.parse(options, args);

            if (commandLine.hasOption("post")) {
                savePostImage = true;
                postImagePath = commandLine.getOptionValue("post");
            }

            if (commandLine.hasOption("d")) {
                doDither = true;
            }

            if (commandLine.hasOption("raw")) {
                saveRawFile = true;
                rawFilePath = commandLine.getOptionValue("raw");
            }

            if (commandLine.hasOption("interlaced")) {
                saveInterlacedFiles = true;
                interlacedFilesPath = commandLine.getOptionValue("interlaced");
            }

            if (commandLine.hasOption("all"))  {
                doAllModes = true;
            }

            if (commandLine.hasOption("com")) {
                saveComFile = true;
                doResize = true;
                comFilePath = commandLine.getOptionValue("com");
            }

            if (commandLine.hasOption("resize")) {
                doResize = true;
            }
            args = commandLine.getArgs(); // reset args to contain JUST the needed information

        } catch (org.apache.commons.cli.ParseException exception) {
            showHelp = true;
        }
        if (args.length < 2) {
            showHelp = true;
        } else {
            try {
                if (doAllModes) {
                    String[] modes = new String[]{"2b0L", "2b0H", "2b1L", "2b1H", "2b5L", "2b5H", "1b",
                            "2Bn0", "2Bn1", "2Bn","2Bo0", "2Bo1", "2Bo"};
                    for (String mode : modes) {
                        String modRawFilePath = insertBeforeFileEx(rawFilePath, "_"+mode);
                        String modPostImagePath = insertBeforeFileEx(postImagePath, "_"+mode);
                        String modInterlacedFilesPath = insertBeforeFileEx(interlacedFilesPath, "_"+mode);
                        String modComFilePath = insertBeforeFileEx(comFilePath, "_"+mode);
                        doIt(mode, args[1], doDither, doResize, saveRawFile, modRawFilePath,
                                savePostImage, modPostImagePath,
                                saveInterlacedFiles, modInterlacedFilesPath, saveComFile, modComFilePath);
                    }
                } else {
                    doIt(args[0], args[1], doDither, doResize, saveRawFile, rawFilePath, savePostImage, postImagePath,
                            saveInterlacedFiles, interlacedFilesPath, saveComFile, comFilePath);
                }
            } catch (IOException e) {
                showHelp = true;
            }
        }
        if (showHelp) {
            // An issue has occurred with the args, print help and exit.
            // args should contain:
            // mode inputFile outputFile
            // Valid modes are:
            // 2b0L, 2b1L, 2b5L - 2bit per pixel palette/mode intensity
            // 2b0H, 2b1H, 2b5H
            // 1b - 1bit per pixel
            // 2Bo0, 2Bo1, 2bo - 2byte per pixel old/new subpalette
            // 2Bn0, 2Bn1, 2Bn
            String header = "Convert an image into a CGA compatible format.\n\n";
            String footer = """
                    --------------Modes-------------
                    320x200 4 color | 2b0L 2b1L 2b5L
                    ...             | 2b0H 2b1H 2b5H
                    640x200 2 color | 1b
                    80 x100 256     | 2Bo0 2bo1 2bn0 2Bn1
                    80 x100 512     | 2Bo       2bn
                    """;

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("cgaimage mode image",
                    header, options, footer, true);
        }
    }

    static void writeToFile(String filename, int[] data) throws IOException {
        FileOutputStream file = new FileOutputStream(filename);
        for (int datum : data) {
            file.write((byte) datum);
        }
        file.close();
    }

    static String insertBeforeFileEx(String filename, String insert) {
        String modFilename = FilenameUtils.removeExtension(filename) + insert;
        if (!Objects.equals(FilenameUtils.getExtension(filename), ""))
            modFilename += "." + FilenameUtils.getExtension(filename);
        return modFilename;
    }

    static void doIt(String mode, String inputFile, boolean doDither, boolean doResize,
                     boolean saveRawFile, String rawFilePath,
                     boolean savePostImage, String postImagePath,
                     boolean saveInterlacedFiles, String interlacedFilesPath,
                     boolean saveComFile, String comFilePath) throws IOException {
        BufferedImage inputImage = ImageIO.read(new File(inputFile));
        if (!(saveRawFile || saveInterlacedFiles || savePostImage || saveComFile)) {
            System.out.println("!!!! No output was specified !!!!");
            return;
        }
        long startTime = System.nanoTime();
        ICGA cgaImage = switch (mode) {
            case "2b0L" -> new Mode2bpp(inputImage, Mode2bpp.PALETTE.LOW_0, doDither, doResize);
            case "2b0H" -> new Mode2bpp(inputImage, Mode2bpp.PALETTE.HIGH_0, doDither, doResize);
            case "2b1L" -> new Mode2bpp(inputImage, Mode2bpp.PALETTE.LOW_1, doDither, doResize);
            case "2b1H" -> new Mode2bpp(inputImage, Mode2bpp.PALETTE.HIGH_1, doDither, doResize);
            case "2b5L" -> new Mode2bpp(inputImage, Mode2bpp.PALETTE.LOW_5, doDither, doResize);
            case "2b5H" -> new Mode2bpp(inputImage, Mode2bpp.PALETTE.HIGH_5, doDither, doResize);
            case "1b" -> new Mode1bpp(inputImage, doDither, doResize);
            case "2Bn0" -> new Mode2Bpp(inputImage, Mode2Bpp.PALETTE.NEW_0, doDither, doResize);
            case "2Bn1" -> new Mode2Bpp(inputImage, Mode2Bpp.PALETTE.NEW_1, doDither, doResize);
            case "2Bn" -> new Mode2Bpp(inputImage, Mode2Bpp.PALETTE.NEW_2, doDither, doResize);
            case "2Bo0" -> new Mode2Bpp(inputImage, Mode2Bpp.PALETTE.OLD_0, doDither, doResize);
            case "2Bo1" -> new Mode2Bpp(inputImage, Mode2Bpp.PALETTE.OLD_1, doDither, doResize);
            case "2Bo" -> new Mode2Bpp(inputImage, Mode2Bpp.PALETTE.OLD_2, doDither, doResize);
            default -> throw new IOException(); // haha, wouldn't it be funny if I lied?
        };
        long endTime = System.nanoTime();
        System.out.println("Quantized image in " + (endTime - startTime)/1000000.0f + "ms.");
        if (saveRawFile) {
            startTime = System.nanoTime();
            int[] data = cgaImage.get();
            writeToFile(rawFilePath, data);
            endTime = System.nanoTime();
            System.out.println("Wrote raw file in " + (endTime - startTime)/1000000.0f + "ms.");
        }
        if (savePostImage) {
            startTime = System.nanoTime();
            ImageIO.write(cgaImage.getImage(), "png", new File(postImagePath));
            endTime = System.nanoTime();
            System.out.println("Wrote post image in " + (endTime - startTime)/1000000.0f + "ms.");
        }
        if (saveInterlacedFiles) {
            if (cgaImage instanceof ICGAInterlaced) {
                startTime = System.nanoTime();
                int[] oddData = ((ICGAInterlaced) cgaImage).getOdd();
                int[] evenData = ((ICGAInterlaced) cgaImage).getEven();
                String oddFile = insertBeforeFileEx(interlacedFilesPath, "_odd");
                String evenFile = insertBeforeFileEx(interlacedFilesPath, "_even");
                writeToFile(oddFile, oddData);
                writeToFile(evenFile, evenData);
                endTime = System.nanoTime();
                System.out.println("Wrote interlaced files in " + (endTime - startTime)/1000000.0f + "ms.");
            } else {
                System.out.println("Mode is not interlaced!");
            }
        }
        if (saveComFile) {
            startTime = System.nanoTime();
            int[] data = cgaImage.getCom();
            writeToFile(comFilePath, data);
            endTime = System.nanoTime();
            System.out.println("Wrote com file in " + (endTime - startTime)/1000000.0f + "ms.");
        }
    }


}
