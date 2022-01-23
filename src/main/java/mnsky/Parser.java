package mnsky;

import mnsky.exceptions.MnskyException;
import mnsky.exceptions.MnskyMissingParameterException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    /**
     * Retrieves and returns the value of the index parameter from the input.
     * @param command The name of the command that called this function.
     * @param inputSplit The input, split into an array using space.
     * @return The value of the index parameter.
     * @throws MnskyMissingParameterException Thrown if the index parameter is missing..
     */
    private static String retrieveIndex(String command, String[] inputSplit) throws MnskyMissingParameterException {
        if (inputSplit.length < 2) {
            throw new MnskyMissingParameterException(command, "index");
        }

        return inputSplit[1];
    }

    /**
     * Creates a new task by parsing the input.
     * @param input The input string.
     * @throws MnskyMissingParameterException Thrown if the name parameter is missing.
     * @return The new task.
     */
    private static ArrayList<String> parseTask(String input) throws MnskyMissingParameterException {
        String[] inputSplit = input.split(" ", 2);
        if (inputSplit.length < 2) {
            throw new MnskyMissingParameterException("todo", "name");
        }

        return new ArrayList<>(List.of("task", inputSplit[1]));
    }

    /**
     * Creates a new deadline (a task with a "by" parameter included) by parsing the input.
     * @param inputSplit The input, split into an array using space.
     * @throws MnskyMissingParameterException Thrown if the name or the by parameter is missing.
     * @return The new deadline.
     */
    private static ArrayList<String> parseDeadline(String[] inputSplit) throws MnskyMissingParameterException {
        if (inputSplit.length < 2) {
            throw new MnskyMissingParameterException("deadline", "name");
        }

        int by_index = 1;
        for (; by_index < inputSplit.length; by_index++) {
            if (inputSplit[by_index].equals("/by")) {
                break;
            }
        }

        if (by_index >= inputSplit.length) {
            throw new MnskyMissingParameterException("deadline", "by");
        }

        String deadlineName = String.join(" ", Arrays.copyOfRange(inputSplit, 1, by_index));
        String by = String.join(" ", Arrays.copyOfRange(inputSplit, by_index + 1, inputSplit.length));

        return new ArrayList<>(List.of("deadline", deadlineName, by));
    }

    /**
     * Creates a new event (a task with an "at" parameter included) by parsing the input.
     * @param inputSplit The input, split into an array using space.
     * @throws MnskyMissingParameterException Thrown if the name or the at parameter is missing.
     * @return The new event.
     */
    private static ArrayList<String> parseEvent(String[] inputSplit) throws MnskyMissingParameterException {
        if (inputSplit.length < 2) {
            throw new MnskyMissingParameterException("event", "name");
        }

        int at_index = 1;
        for (; at_index < inputSplit.length; at_index++) {
            if (inputSplit[at_index].equals("/at")) {
                break;
            }
        }

        if (at_index >= inputSplit.length) {
            throw new MnskyMissingParameterException("event", "at");
        }

        String eventName = String.join(" ", Arrays.copyOfRange(inputSplit, 1, at_index));
        String at = String.join(" ", Arrays.copyOfRange(inputSplit, at_index + 1, inputSplit.length));

        return new ArrayList<>(List.of("event", eventName, at));
    }

    /**
     * Parses all the tasks in the storage data.
     * @param rawTaskList
     * @return
     * @throws MnskyException
     */
    public static ArrayList<ArrayList<String>> parseStorageData(ArrayList<String> rawTaskList) throws MnskyException {
        try {
            ArrayList<ArrayList<String>> taskList = new ArrayList<>();

            for (String line : rawTaskList) {
                String[] lineSplit = line.split(" ");
                ArrayList<String> nextTask = null;

                if (line.charAt(1) == 'T') {
                    nextTask = parseTask(line);
                } else if (line.charAt(1) == 'D') {
                    nextTask = parseDeadline(lineSplit);
                } else if (line.charAt(1) == 'E') {
                    nextTask = parseEvent(lineSplit);
                }

                if (nextTask != null) {
                    if (nextTask.size() < 3) {
                        nextTask.add("");
                    }
                    nextTask.add(line.substring(4, 5));
                    taskList.add(nextTask);
                }
            }

            return taskList;
        } catch (MnskyException e) {
            throw new MnskyException("[MNSKY is having trouble remembering the previous task list...]\n");
        }
    }

    /**
     * Parses the input and executes the logic depending on the type of input.
     * @return True if the user input "bye" and thus wants to stop talking to the chatbot.
     *          False otherwise.
     */
    public static ArrayList<String> parseInput(String input) throws MnskyException {
        String[] inputSplit = input.split(" ");
        ArrayList<String> parsedInput = new ArrayList<>();

        switch (inputSplit[0]) {
            case "bye":
                parsedInput.add("bye");
                break;

            case "list":
                parsedInput.add("list");
                break;

            case "mark":
                parsedInput.add("mark");
                parsedInput.add(retrieveIndex("mark", inputSplit));
                break;

            case "unmark":
                parsedInput.add("unmark");
                parsedInput.add(retrieveIndex("unmark", inputSplit));
                break;

            case "todo":
                parsedInput = parseTask(input);
                break;

            case "event":
                parsedInput = parseEvent(inputSplit);
                break;

            case "deadline":
                parsedInput = parseDeadline(inputSplit);
                break;

            case "delete":
                parsedInput.add("delete");
                parsedInput.add(retrieveIndex("delete", inputSplit));
                break;

            default:
                parsedInput.add("invalid");
        }

        return parsedInput;
    }
}
