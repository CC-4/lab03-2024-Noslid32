/*
    Laboratorio No. 3 - Recursive Descent Parsing
    CC4 - Compiladores

    Clase que representa el parser

    Actualizado: agosto de 2021, Luis Cu
*/

import java.util.LinkedList;
import java.util.Stack;

public class Parser {

    // Puntero next que apunta al siguiente token
    private int next;
    // Stacks para evaluar en el momento
    private Stack<Double> operandos;
    private Stack<Token> operadores;
    // LinkedList de tokens
    private LinkedList<Token> tokens;

    // Funcion que manda a llamar main para parsear la expresion
    public boolean parse(LinkedList<Token> tokens) {
        this.tokens = tokens;
        this.next = 0;
        this.operandos = new Stack<Double>();
        this.operadores = new Stack<Token>();

        // Recursive Descent Parser
        // Imprime si el input fue aceptado
        boolean aceptada = S();
        System.out.println("Aceptada? " + aceptada);

        // Shunting Yard Algorithm
        // Imprime el resultado de operar el input
        if (aceptada && !this.operandos.isEmpty()) {
            System.out.println("Resultado: " + this.operandos.peek());
        }

        // Verifica si terminamos de consumir el input
        if(this.next != this.tokens.size()) {
            return false;
        }
        return aceptada;
    }

    // Verifica que el id sea igual que el id del token al que apunta next
    // Si si avanza el puntero es decir lo consume.
    private boolean term(int id) {
        if(this.next < this.tokens.size() && this.tokens.get(this.next).getId() == id) {
            if (id == Token.NUMBER) {
                operandos.push(this.tokens.get(this.next).getVal());
            } else if (id == Token.SEMI) {
                while (!this.operadores.empty()) {
                    popOp();
                }
            } else if (id != Token.LPAREN && id != Token.RPAREN) {
                pushOp(this.tokens.get(this.next));
            }
            this.next++;
            return true;
        }
        return false;
    }

    // Funcion que verifica la precedencia de un operador
    private int pre(Token op) {
        switch(op.getId()) {
            case Token.LPAREN:
                return 0;
            case Token.PLUS:
            case Token.MINUS:
                return 1;
            case Token.MULT:
            case Token.DIV:
            case Token.MOD:
                return 2;
            case Token.EXP:
                return 3;
            case Token.UNARY:
                return 4;
            default:
                return -1;
        }
    }

    private void popOp() {
        if (this.operadores.isEmpty()) {
            return;
        }
        Token op = this.operadores.pop();
        double a, b;

        switch(op.getId()) {
            case Token.PLUS:
                if (this.operandos.size() < 2) return;
                b = this.operandos.pop();
                a = this.operandos.pop();
                this.operandos.push(a + b);
                break;
            case Token.MINUS:
                if (this.operandos.size() < 2) return;
                b = this.operandos.pop();
                a = this.operandos.pop();
                this.operandos.push(a - b);
                break;
            case Token.MULT:
                if (this.operandos.size() < 2) return;
                b = this.operandos.pop();
                a = this.operandos.pop();
                this.operandos.push(a * b);
                break;
            case Token.DIV:
                if (this.operandos.size() < 2) return;
                b = this.operandos.pop();
                a = this.operandos.pop();
                this.operandos.push(a / b);
                break;
            case Token.MOD:
                if (this.operandos.size() < 2) return;
                b = this.operandos.pop();
                a = this.operandos.pop();
                this.operandos.push(a % b);
                break;
            case Token.EXP:
                if (this.operandos.size() < 2) return;
                b = this.operandos.pop();
                a = this.operandos.pop();
                this.operandos.push(Math.pow(a, b));
                break;
            case Token.UNARY:
                if (this.operandos.isEmpty()) return;
                a = this.operandos.pop();
                this.operandos.push(-a);
                break;
        }
    }

    private void pushOp(Token op) {
        while (!this.operadores.empty() && 
               pre(this.operadores.peek()) >= pre(op) &&
               this.operadores.peek().getId() != Token.LPAREN) {
            popOp();
        }
        this.operadores.push(op);
    }

    private boolean S() {
        return E() && term(Token.SEMI);
    }

    private boolean E() {
        if (T()) {
            while (term(Token.PLUS) || term(Token.MINUS)) {
                if (!T()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean T() {
        if (F()) {
            while (term(Token.MULT) || term(Token.DIV) || term(Token.MOD)) {
                if (!F()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean F() {
        if (term(Token.MINUS)) {
            if (P()) {
                operandos.push(-operandos.pop());
                return true;
            }
            return false;
        } else if (P()) {
            if (term(Token.EXP)) {
                return F();
            }
            return true;
        }
        return false;
    }

    private boolean P() {
        if (term(Token.LPAREN)) {
            if (E() && term(Token.RPAREN)) {
                return true;
            }
            return false;
        } else if (term(Token.NUMBER)) {
            return true;
        }
        return false;
    }

    }