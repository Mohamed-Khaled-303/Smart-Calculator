package calculator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Stack;

public class Calculator {
    public static BigInteger calculate(ArrayList<String> postfixExpression){
        Stack<BigInteger> stack = new Stack<>();
        final String regexSign = "[+\\-*/]";
        for (int i = 0; i < postfixExpression.size(); i++) {
            String incomingElement = postfixExpression.get(i);

            if (incomingElement.matches("[0-9]+|-[0-9]+"))
                stack.push(new BigInteger(incomingElement));
            if (incomingElement.matches(regexSign)) {
                BigInteger secondNumber = stack.pop();
                BigInteger firstNumber = stack.pop();
                if (incomingElement.equals("+")) {
                    BigInteger result = firstNumber.add(secondNumber);
                    stack.push(result);
                }
                if (incomingElement.equals("-")){
                    BigInteger result = firstNumber.subtract(secondNumber);
                    stack.push(result);
                }

                if (incomingElement.equals("*")) {
                    BigInteger result = firstNumber.multiply(secondNumber);
                    stack.push(result);
                }
                if (incomingElement.equals("/")){
                    BigInteger result = firstNumber.divide(secondNumber);
                    stack.push(result);
                }

            }
        }
        return stack.pop();
    }
}