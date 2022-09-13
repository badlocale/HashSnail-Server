package org.hashsnail.server.cli;

import org.apache.commons.cli.*;
import org.hashsnail.server.model.Algorithm;
import org.hashsnail.server.model.mods.AttackByDictionary;
import org.hashsnail.server.model.mods.AttackByMask;
import org.hashsnail.server.model.mods.AttackMode;
import org.hashsnail.server.model.mods.ClassicBruteforce;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ArgumentsParser {
    public CommandLine cmd;

    public ArgumentsParser(String[] args) {
        CommandLineParser parser = new DefaultParser();

        try {
            cmd = parser.parse(createParseOptions(), args);
        } catch (ParseException e) {
            e.printStackTrace();
            System.err.println("[CLI-parser] Cant read your arguments. You may be running the program incorrectly.");
            System.exit(-1);
        }
    }

    private Options createParseOptions() {
        Options options = new Options();

        Option notDefaultPortOption = new Option("p", "port", true, "port");
        notDefaultPortOption.setArgs(1);
        notDefaultPortOption.setOptionalArg(true);
        notDefaultPortOption.setArgName("port for listening");

        Option algorithmToAttackOption = new Option("a", "algorithm", true, "hash algoritm");
        algorithmToAttackOption.setArgs(1);
        algorithmToAttackOption.setOptionalArg(false);
        algorithmToAttackOption.setArgName("hash algoritm naming");

        Option bruteforceAttackOption = new Option("b", "brute-force", true, "use brute-force");
        bruteforceAttackOption.setArgs(1);
        bruteforceAttackOption.setOptionalArg(false);
        bruteforceAttackOption.setArgName("word length");

        Option maskAttackOption = new Option("m", "mask", true, "use attack by mask");
        maskAttackOption.setArgs(1);
        maskAttackOption.setOptionalArg(false);
        maskAttackOption.setArgName("mask");

        Option dictionaryAttackOption = new Option("d", "dictionary", true, "use dictionary attack");
        dictionaryAttackOption.setArgs(1);
        dictionaryAttackOption.setOptionalArg(true);
        dictionaryAttackOption.setArgName("dictionary directory");

        OptionGroup attackModsGroup = new OptionGroup();
        attackModsGroup.addOption(bruteforceAttackOption);
        attackModsGroup.addOption(maskAttackOption);
        attackModsGroup.addOption(dictionaryAttackOption);

        Option isSingleHashOption = new Option("s", "single", true, "take only one hash");
        isSingleHashOption.setArgs(1);
        isSingleHashOption.setOptionalArg(false);
        isSingleHashOption.setArgName("hash");

        Option isMultipleHashOption = new Option("c", "hash-collection", true, "take a collection of hashes");
        isMultipleHashOption.setArgs(1);
        isMultipleHashOption.setOptionalArg(false);
        isMultipleHashOption.setArgName("directory path");

        OptionGroup hashNumberGroup = new OptionGroup();
        hashNumberGroup.addOption(isSingleHashOption);
        hashNumberGroup.addOption(isMultipleHashOption);

        options.addOption(notDefaultPortOption);
        options.addOption(algorithmToAttackOption);
        options.addOptionGroup(attackModsGroup);
        options.addOptionGroup(hashNumberGroup);

        return options;
    }

    public int parsePort() {
        if (cmd.hasOption("port")) {
            return Integer.parseInt(cmd.getOptionValue("port"));
        }

        return 8000;
    }

   public Algorithm parseAlgorithm() {
       Algorithm algorithm = null;

       if (cmd.hasOption("algoritm")) {
           String algoritmName = cmd.getOptionValue("algoritm");

           algorithm = switch (algoritmName) {
               case "SHA1" -> new Algorithm("SHA1", 1, 40);
               default -> new Algorithm("MD5", 0, 32);
           };
       }

       return algorithm;
   }

   public Path parseHashFilePath() {
        Path hashFilePath = null;

       if (cmd.hasOption("hash-collection")) {
           hashFilePath = Paths.get(cmd.getOptionValue("hash-collection"));

           if (!Files.isReadable(hashFilePath) || hashFilePath == null) {
               throw new InvalidPathException(hashFilePath.toString(), "[CLI-parser] Not readable hash file format.");
           }
       }

       return hashFilePath;
   }

   public String parseSingleHash() {
       String hash = null;

       if (cmd.hasOption("single")) {
           hash = cmd.getOptionValue("single");

           if (!parseAlgorithm().isValidHashes(hash)) {
               System.err.println("[CLI-parser] Not correct hash value.");
           }
       }

       return hash;
   }

   public AttackMode parseAttackMode() {
       AttackMode attackMode = null;

       if (cmd.hasOption("brute-force")) {
           int numberOfElements = Integer.parseInt(cmd.getOptionValue("brute-force"));
           attackMode = new ClassicBruteforce(numberOfElements);
       }

       if (cmd.hasOption("mask")) {
           String mask = cmd.getOptionValue("mask");
           attackMode = new AttackByMask(mask);
       }

       if (cmd.hasOption("dictionary")) {
           Path dictionaryPath;

           if (cmd.getOptionValue("dictionary") != null) {
               dictionaryPath = Paths.get(cmd.getOptionValue("dictionary"));

               if (!Files.isReadable(dictionaryPath)) {
                   throw new InvalidPathException(dictionaryPath.toString(),
                           "[CLI-parser] Not readable dictionary path format.");
               }
           } else {
               dictionaryPath = Paths.get("dictionary.txt");
           }

           attackMode = new AttackByDictionary(dictionaryPath);
       }

       return attackMode;
   }
}
