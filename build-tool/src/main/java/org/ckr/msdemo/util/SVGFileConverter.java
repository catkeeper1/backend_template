package org.ckr.msdemo.util;

import org.apache.batik.apps.rasterizer.*;
import org.apache.tools.ant.DirectoryScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SVGFileConverter {

    private File destinationFolder = new File("./converted_folder");

    private File sourceFolder = new File("./svg_folder");

    private List<String> includeFilter = new ArrayList<>();

    private List<String> excludeFilter = null;

    private DestinationType destinationType = DestinationType.PNG;

    private boolean doNotChangeSuffix = false;

    private SVGFileConverter() {

        super();
        includeFilter.add("**/*.svg");
    }

    public static SVGFileConverter getInstance() {
        return new SVGFileConverter();
    }

    public SVGFileConverter setDestinationFolder(File destinationFolder) {
        this.destinationFolder = destinationFolder;

        if(!destinationFolder.exists()) {
            destinationFolder.mkdir();
        }

        if(destinationFolder.isFile()) {
            throw new RuntimeException("Destination folder "+ destinationFolder.getAbsolutePath() +" is a file.");
        }

        return this;
    }

    public SVGFileConverter setDestinationFolder(String destinationFolderPath) {
        return setDestinationFolder(new File(destinationFolderPath));

    }

    public SVGFileConverter setSourceFolder(File sourceFolder) {
        this.sourceFolder = sourceFolder;

        if(!sourceFolder.exists()) {
            throw new RuntimeException("Source folder " + sourceFolder.getAbsolutePath() +" is not exist.");
        }

        if(sourceFolder.isFile()) {
            throw new RuntimeException("Source folder " + sourceFolder.getAbsolutePath() + " is a file.");
        }

        return this;
    }

    public SVGFileConverter setSourceFolder(String sourceFolder) {
        return setSourceFolder(new File(sourceFolder));
    }

    public SVGFileConverter setIncludeFilter(List<String> filter) {
        this.includeFilter = filter;
        return this;
    }

    public SVGFileConverter setExcludeFilter(List<String> excludeFilter) {
        this.excludeFilter = excludeFilter;
        return this;
    }

    public SVGFileConverter setDestinationType(DestinationType destinationType) {
        this.destinationType = destinationType;
        return this;
    }

    public SVGFileConverter setDoNotChangeSuffix(boolean doNotChangeSuffix) {
        this.doNotChangeSuffix = doNotChangeSuffix;

        return this;
    }

    public List<File> execute() {
        DirectoryScanner scanner = new DirectoryScanner();

        if(includeFilter != null) {
            scanner.setIncludes(includeFilter.toArray(new String[includeFilter.size()]));
        }

        if(excludeFilter != null) {
            scanner.setExcludes(excludeFilter.toArray(new String[excludeFilter.size()]));
        }
        scanner.setBasedir(sourceFolder);
        scanner.setCaseSensitive(false);
        scanner.scan();

        String[] includeFiles = scanner.getIncludedFiles();
        ArrayList<File> result = new ArrayList<>();

        for(String includeFile: includeFiles) {

            SVGConverter converter =
                    new SVGConverter(new ConvertController(this.destinationType, this.doNotChangeSuffix));

            File destFile = new File(this.destinationFolder, includeFile);
            destFile = destFile.getParentFile();

            File srcFile = new File(this.sourceFolder, includeFile);



            converter.setDst(destFile);
            converter.setSources(new String[]{srcFile.getAbsolutePath()});

            if(this.destinationType != null) {
                converter.setDestinationType(this.destinationType);
            }

            try {
                converter.execute();
            } catch (SVGConverterException e) {
                throw new RuntimeException("", e);
            }



            result.add(srcFile);
        }


        return result;

    }

    public static void main(String[] args) {

        if(args.length != 2) {
            throw new RuntimeException("Need to provide 2 parameters for this main method." +
                                       "The first one is the source folder. The second one is the destination folder");
        }

        getInstance()
                .setDestinationFolder(args[1])
                .setSourceFolder(args[0])
                .setDestinationType(DestinationType.PNG)
                .setDoNotChangeSuffix(false)
                .execute();
    }

    public static class ConvertController extends DefaultSVGConverterController {

        private DestinationType destinationType;

        private boolean doNotChangeSuffix;

        public ConvertController(DestinationType destinationType, boolean doNotChangeSuffix) {
            this.destinationType = destinationType;
            this.doNotChangeSuffix = doNotChangeSuffix;
        }



        @Override
        public void onSourceTranscodingSuccess(SVGConverterSource source, File dest) {
            super.onSourceTranscodingSuccess(source, dest);
            if(this.doNotChangeSuffix) {
                return;
            }

            String orgName = source.getName();
            int lastDoc = orgName.lastIndexOf(".");

            if(lastDoc > 0) {

                String orgSuffix = orgName.substring(lastDoc, orgName.length());

                String newFilePath = dest.getAbsolutePath().substring(0,
                        (dest.getAbsolutePath().length() - (destinationType.getExtension().length())) );

                newFilePath = newFilePath + orgSuffix;

                //if the file is already exist, cannot rename. so that need to delete it first.
                File existingFile = new File(newFilePath);
                if(existingFile.exists()) {
                    existingFile.delete();

                }

                dest.renameTo(new File(newFilePath));
            }



        }
    }
}
