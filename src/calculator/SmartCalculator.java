package calculator;

import java.util.*;

public class SmartCalculator {
    private String userInput;
    private String[] expression;
    private Map<String, String> variables = new HashMap<>();
    private ArrayList<String> postfixNotation = new ArrayList<>();
    private Stack<String> reordering = new Stack<>();


    public void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("write expression to be calculated or Assign New Variable or write /exit to exit or writ /help to get help");
        while (true) {
            userInput = scanner.nextLine();

            if (userInput.isEmpty())
                continue;

            if (isCommand()) {
                printMessage();
                if (isExitCommand())
                    break;
                continue;
            }

            if (isUserAssignNewVariable()){
                assignNewVariable();
                continue;
            }
            calculateExpression();
        }
    }
    private void calculateExpression() {
        userInputCorrection();
        expression = userInput.split(" ");
        for (var i = 0; i < expression.length; i++){
            if (expression[i].matches("[a-zA-Z]+"))
                expression[i] = String.valueOf(variables.getOrDefault(expression[i], "Unknown variable"));
        }

        if (isValidExpression()) {
            convertExpressionToPostfixNotation();
            System.out.println(Calculator.calculate(postfixNotation));
            postfixNotation.clear();
        }
        else if (!isKnownVariable())
            System.out.println("Unknown variable");
        else
            System.out.println("Invalid expression");
    }

    private void convertExpressionToPostfixNotation() {
        final String regexNumber = "[0-9]+|-[0-9]+";
        final String regexOperation = "[+\\-()/*]";

        for (var i = 0; i < expression.length; i++) {
            if (expression[i].matches(regexNumber))
                postfixNotation.add(expression[i]);
            if (expression[i].matches(regexOperation)){
                if (reordering.isEmpty() || reordering.peek().equals("("))
                    reordering.push(expression[i]);

                else if (expression[i].equals("("))
                    reordering.push(expression[i]);

                else if (expression[i].equals(")")) {
                    while (!reordering.peek().equals("("))
                        postfixNotation.add(reordering.pop());
                    reordering.pop();
                }

                else if (isEqualPrecedence(expression[i], reordering.peek())) {
                    while (!reordering.isEmpty() && isEqualPrecedence(expression[i], reordering.peek())) {
                        if (reordering.peek().equals("("))
                            break;
                        postfixNotation.add(reordering.pop());
                    }
                    reordering.push(expression[i]);
                }

                else if (havePrecedence(expression[i], reordering.peek()))
                    reordering.push(expression[i]);


                else if (!havePrecedence(expression[i], reordering.peek())) {
                    while (!reordering.isEmpty() && !havePrecedence(expression[i], reordering.peek())) {
                        if (reordering.peek().equals("("))
                            break;
                        postfixNotation.add(reordering.pop());
                    }
                    reordering.push(expression[i]);
                }
            }
        }

        while (!reordering.isEmpty()){
            postfixNotation.add(reordering.pop());
        }
    }
    private boolean havePrecedence(String  incomingOperator, String operatorOnTopStack) {
        if (incomingOperator.equals("*") && operatorOnTopStack.equals("+") ||
                incomingOperator.equals("*") && operatorOnTopStack.equals("-"))
            return true;
        else if (incomingOperator.equals("/") && operatorOnTopStack.equals("+") ||
                incomingOperator.equals("/") && operatorOnTopStack.equals("-")) {
            return true;
        } else
            return false;
    }

    private boolean isEqualPrecedence(String incomingOperator, String operatorOnTopStack) {
        if (incomingOperator.equals("+") && operatorOnTopStack.equals("+") ||
                incomingOperator.equals("+") && operatorOnTopStack.equals("-"))
            return true;
        else if (incomingOperator.equals("-") && operatorOnTopStack.equals("+") ||
                incomingOperator.equals("-") && operatorOnTopStack.equals("-")) {
            return true;
        } else if (incomingOperator.equals("*") && operatorOnTopStack.equals("*") ||
                incomingOperator.equals("*") && operatorOnTopStack.equals("/")) {
            return true;
        } else if (incomingOperator.equals("/") && operatorOnTopStack.equals("*") ||
                incomingOperator.equals("/") && operatorOnTopStack.equals("/")) {
            return true;
        }else
            return false;
    }

    private boolean isKnownVariable() {
        for (var operation : expression)
            if (operation.equals("Unknown variable"))
                return false;
        return true;
    }

    private void printHelpMessage() {
        System.out.println("The program calculates any expression you can write and you can assign variables");
    }

    private boolean isValidExpression() {
        final String regexSign = "[+\\-*/]";
        final String regexNumber = "\\+[0-9]+|[0-9]+|-[0-9]+";
        int numbersCounter = 0;
        int signsCounter = 0;
        Stack<String> stack = new Stack<>();

        for (var operation : expression) {
            if (operation.matches(regexNumber))
                numbersCounter += 1;
            else if (operation.matches(regexSign))
                signsCounter += 1;
            else if (operation.equals("Unknown variable"))
                return false;
            else if (operation.equals("("))
                stack.push(operation);
            else if (operation.equals(")") && stack.isEmpty())
                return false;
            else if (operation.equals(")"))
                stack.pop();
        }

        return numbersCounter > signsCounter && stack.isEmpty();
    }

    private void userInputCorrection() {
        final String regexWhiteSpaces = "\\s+";
        final String regexPlusSinesMoreThanOne = "\\+{2,}";
        final String regexMinusSinesMoreThanTwo = "-{3,}";
        final String regexMinusSinesExactlyTwo = "-{2}";
        final String regexPlusMinus = "\\+-+";

        userInput = userInput.replaceAll(regexMinusSinesExactlyTwo, "+").
                replaceAll(regexPlusMinus, "-").
                replaceAll(regexPlusSinesMoreThanOne, "+").
                replaceAll(regexMinusSinesMoreThanTwo, "-");

        userInput = userInput.replaceAll("-", " -").
                replaceAll("\\+", " + ").
                replaceAll("\\*", " * ").
                replaceAll("/", " / ").
                replaceAll("\\(", "( ").
                replaceAll("\\)", " )");
        userInput.replaceAll("\\s{2,}", " ");
    }

    private boolean isCommand() {
        return userInput.matches("/.+");
    }

    private boolean isValidCommand() {
        return isHelpCommand() || isExitCommand();
    }

    private void printMessage() {
        if (isValidCommand()){
            if (isHelpCommand())
                printHelpMessage();
            if (isExitCommand())
                printExitMessage();
        }
        else
            System.out.println("Unknown command");
    }

    private boolean isHelpCommand() {
        return userInput.equals("/help");
    }

    private boolean isExitCommand() {
        return userInput.equals("/exit");
    }

    private void printExitMessage() {
        System.out.println("Bye!");
    }

    private boolean isUserAssignNewVariable() {
        return userInput.contains("=");
    }

    private void assignNewVariable() {
        final String regexWhiteSpaces = "\s";
        userInput = userInput.replaceAll(regexWhiteSpaces, "");
        if (isValidIdentifier()) {
            if (validAssignment()) {
                expression = userInput.split("=");
                String variable = expression[0];
                String value = "";
                if (expression[1].matches("[a-zA-Z]+")) {
                    if (variables.containsKey(expression[1]))
                        value = variables.get(expression[1]);
                    else {
                        System.out.println("Invalid assignment");
                        return;
                    }
                }
                else
                    value = expression[1];
                variables.put(variable, value);
            }
            else
                System.out.println("Invalid assignment");
        }
        else
            System.out.println("Invalid identifier");
    }

    private boolean isValidIdentifier(){
        return userInput.matches("[a-zA-Z]+=.+");
    }

    private boolean validAssignment() {
        return userInput.matches("[a-zA-Z]+=[0-9]+|[a-zA-Z]+=[a-zA-Z]+|[a-zA-Z]+=-[0-9]+");
    }
}