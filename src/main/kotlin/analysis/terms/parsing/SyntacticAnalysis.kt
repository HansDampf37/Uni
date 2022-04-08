package analysis.terms.parsing

import analysis.terms.*
import analysis.terms.model.*
import toNum

/**
 * Term parser
 *
 * Term -> Summand Sum
 * Sum -> eps | + Summand Sum (+) | - Summand Sum (-)
 * Summand -> Faktor Product
 * Product -> eps | * Factor Product (*) | / Factor Product (/)
 * Factor -> Leaf Potency
 * Potency -> eps | ^ Leaf Potency (^)
 * Leaf -> (Term)  (()| Variable | Number | log arg arg | sin arg | cos arg | tan arg | log_ arg
 * arg -> (Term)
 *
 */
class SyntacticAnalysis {
    private lateinit var lexer: Lexer
    private lateinit var assignment: MutableList<String>

    fun parse(tokenStream: Array<Token>, assignment: List<String>): Term {
        lexer = Lexer(tokenStream)
        this.assignment = assignment.toMutableList()
        return parseTerm()
    }

    private fun expect(token: Token) {
        if (lexer.current() == token) lexer.lex() else lexer.error(token)
    }

    private fun parseTerm(): Term {
        val left = parseSummand()
        return parseSum(left)
    }

    private fun parseSum(left: Term): Term {
        val addSummand: (res: Term, summand: Term) -> Term = { res, summand ->
            val res1: Term
            if (res is Sum) {
                res1 = res
                res1.add(summand)
            } else {
                res1 = Sum(res, summand)
            }
            res1
        }
        var res = left
        while (true) {
            when (lexer.current()) {
                Token.PLUS -> {
                    expect(Token.PLUS)
                    res = addSummand(res, parseSummand())
                    continue
                }
                Token.MINUS -> {
                    expect(Token.MINUS)
                    res = addSummand(res, parseSummand().inverseAdd())
                    continue
                }
                Token.STAR, Token.SLASH, Token.CLOSE_BRACKET, Token.EOF -> {
                    return res
                }
                else -> throw IllegalStateException("unexpected input ${lexer.current()}")
            }
        }
    }

    private fun parseSummand(): Term {
        val left = parseFactor()
        return parseProduct(left)
    }

    private fun parseProduct(left: Term): Term {
        val addFactor: (res: Term, factor: Term) -> Term = { res, factor ->
            val res1: Term
            if (res is Product) {
                res1 = res
                res1.add(factor)
            } else {
                res1 = Product(res, factor)
            }
            res1
        }
        var res = left
        while (true) {
            when (lexer.current()) {
                Token.STAR -> {
                    expect(Token.STAR)
                    res = addFactor(res, parseFactor())
                    continue
                }
                Token.SLASH -> {
                    expect(Token.SLASH)
                    res = addFactor(res, parseFactor().inverseMult())
                    continue
                }
                Token.VAR -> {
                    res = addFactor(res, parseFactor())
                    continue
                }
                Token.OPEN_BRACKET -> {
                    expect(Token.OPEN_BRACKET)
                    res = addFactor(res, parseTerm())
                    expect(Token.CLOSE_BRACKET)
                    continue
                }
                Token.PLUS, Token.MINUS, Token.CLOSE_BRACKET, Token.EOF -> {
                    return res
                }
                else -> throw IllegalStateException("unexpected input ${lexer.current()}")
            }
        }
    }

    private fun parseFactor(): Term {
        val left = parseLeaf()
        return parsePotency(left)
    }

    private fun parsePotency(left: Term): Term {
        var res = left
        while (true) {
            when (lexer.current()) {
                Token.POWER -> {
                    expect(Token.POWER)
                    res = Power(res, parseLeaf())
                    continue
                }
                Token.STAR, Token.SLASH, Token.PLUS, Token.MINUS, Token.CLOSE_BRACKET, Token.EOF, Token.VAR, Token.OPEN_BRACKET -> {
                    return res
                }
                else -> throw IllegalStateException("unexpected input ${lexer.current()}")
            }
        }
    }

    private fun parseLeaf(): Term {
        if (lexer.current() == Token.POSITIVE) expect(Token.POSITIVE)
        else if (lexer.current() == Token.NEGATIVE) {
            expect(Token.NEGATIVE)
            val leaf = parseLeaf()
            return leaf.inverseAdd()
        }
        when (lexer.current()) {
            Token.OPEN_BRACKET -> {
                expect(Token.OPEN_BRACKET)
                val term = parseTerm()
                expect(Token.CLOSE_BRACKET)
                return term
            }
            Token.SIN -> {
                expect(Token.SIN)
                throw NotImplementedError()
            }
            Token.COS -> {
                expect(Token.COS)
                throw NotImplementedError()
            }
            Token.TAN -> {
                expect(Token.TAN)
                throw NotImplementedError()
            }
            Token.LOG -> {
                expect(Token.LOG)
                val base = parseArg()
                val arg = parseArg()
                return Log(base, arg)
            }
            Token.LOG_2 -> {
                expect(Token.LOG_2)
                val arg = parseArg()
                return Log(two, arg)
            }
            Token.LN -> {
                expect(Token.LN)
                val arg = parseArg()
                return Ln(arg)
            }
            Token.LOG_10 -> {
                expect(Token.LOG_10)
                val arg = parseArg()
                return Log(Num(10), arg)
            }
            Token.VAR -> {
                expect(Token.VAR)
                val name = assignment.removeAt(0)
                return Variable(name)
            }
            Token.NUM -> {
                expect(Token.NUM)
                return assignment.removeAt(0).toNum()
            }
            else -> throw IllegalStateException("unexpected input ${lexer.current()}")
        }
    }

    private fun parseArg(): Term {
        expect(Token.OPEN_BRACKET)
        val t = parseTerm()
        expect(Token.CLOSE_BRACKET)
        return t

    }

    private inner class Lexer(val tokenStream: Array<Token>) {
        private var currentIndex: Int = 0
        fun atLookahead(i: Int): Token {
            assert(i + currentIndex < tokenStream.size)
            assert(i > 0)
            return tokenStream[currentIndex + i]
        }

        fun current(): Token {
            return tokenStream[currentIndex]
        }

        fun lex() {
            currentIndex++
        }

        fun error(expected: Token) {
            throw IllegalStateException("expected token $expected but got ${current()} at $currentIndex of ${tokenStream.map { it.name }}")
        }
    }
}

fun main() {
    val str = "x + -y * (-x / - log(10)(x) - 2^alpha)"
    val message = LexiAnalysis().parse(str)
    println(str)
    println(message.first.map { it.name })
    println(message.second)
    val term = SyntacticAnalysis().parse(message.first, message.second)
    println(term)
    //println(term.simplify())
}