package analysis.terms

/**
 * Term parser
 *
 * Term -> Summand Sum
 * Sum -> eps | + Summand Sum (+) | - Summand Sum (-)
 * Summand -> Faktor Product
 * Product -> eps | * Factor Product (*) | / Factor Product (/)
 * Factor -> Leaf Potency
 * Potency -> eps | ^ Leaf Potency (^)
 * Leaf -> (Term)  (()| Variable | Number | log arg arg | sin arg | cos arg | tan arg
 * arg -> (Term)
 *
 */
class SyntacticAnalysis {
    private lateinit var lexer: Lexer
    private lateinit var assignment: MutableList<String>

    private fun expect(token: Token) {
        if (lexer.current() == token) lexer.lex() else lexer.error(token)
    }

    fun parse(tokenStream: Array<Token>, assignment: List<String>): Term {
        lexer = Lexer(tokenStream)
        this.assignment = assignment.toMutableList()
        return parseTerm()
    }

    private fun parseTerm(): Term {
        val left = parseSummand()
        return parseSum(left)
    }

    private fun parseSum(left: Term): Term {
        var res = left
        while (true) {
            when (lexer.current()) {
                Token.PLUS -> {
                    expect(Token.PLUS)
                    res = Sum(res, parseSummand())
                    continue
                }
                Token.MINUS -> {
                    expect(Token.MINUS)
                    res = Sum(left, parseSummand().inverseAdd())
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
        var res = left
        while (true) {
            when (lexer.current()) {
                Token.STAR -> {
                    expect(Token.STAR)
                    res = Product(res, parseFactor())
                    continue
                }
                Token.SLASH -> {
                    expect(Token.SLASH)
                    res = Product(res, parseFactor().inverseMult())
                    continue
                }
                Token.VAR -> {
                    res = Product(res, parseFactor())
                    continue
                }
                Token.OPEN_BRACKET -> {
                    expect(Token.OPEN_BRACKET)
                    res = Product(res, parseTerm())
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
                    res = Power(left, parseLeaf())
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
            Token.VAR -> {
                expect(Token.VAR)
                val name = assignment.removeAt(0)
                return Variable(name)
            }
            Token.NUM -> {
                expect(Token.NUM)
                val num = assignment.removeAt(0).toDouble()
                return Num(num)
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
            throw IllegalStateException("expected token $expected but got ${current()}")
        }
    }
}

class LexiAnalysis {
    fun parse(str: String): Pair<Array<Token>, MutableList<String>> {
        val assignment = ArrayList<String>()
        val strWithoutSpaces = str.replace(" ", "")
        val tokenList = ArrayList<Token>()
        var current = 0
        var delta = strWithoutSpaces.length
        while (true) {
            val substr = strWithoutSpaces.subSequence(current, current + delta)
            val tokens =
                Token.values().filter { it.regex.matches(substr) }
            if (tokens.count() == 1) {
                val token = Token.values().first { it.regex.matches(substr) }
                tokenList.add(token)
                if (token == Token.VAR || token == Token.NUM) assignment.add(substr.toString())
                current += delta
                delta = strWithoutSpaces.length - current
            } else if (tokens.isEmpty()) {
                delta--
            } else {
                if (tokens.size == 2) {
                    if (tokens.contains(Token.POSITIVE) && tokens.contains(Token.PLUS)) {
                        val last = tokenList.last()
                        val token = if (last == Token.CLOSE_BRACKET || last == Token.NUM || last == Token.VAR) {
                            Token.PLUS
                        } else {
                            Token.POSITIVE
                        }
                        tokenList.add(token)
                        current += delta
                        delta = strWithoutSpaces.length - current
                    } else if (tokens.contains(Token.NEGATIVE)&& tokens.contains(Token.MINUS)) {
                        val last = tokenList.last()
                        val token = if (last == Token.CLOSE_BRACKET || last == Token.NUM || last == Token.VAR) {
                            Token.MINUS
                        } else {
                            Token.NEGATIVE
                        }
                        tokenList.add(token)
                        current += delta
                        delta = strWithoutSpaces.length - current
                    } else {
                        throw java.lang.IllegalStateException("Unimplemented Conflict in $substr: $tokens")
                    }
                }
            }
            if (current == strWithoutSpaces.length) break
            if (delta < 0) throw IllegalStateException()
        }
        tokenList.add(Token.EOF)
        return Pair(Array(tokenList.size) { tokenList[it] }, assignment)
    }
}

enum class Token(val regex: Regex) {
    NEGATIVE(Regex("-")),
    POSITIVE(Regex("\\+")),
    STAR(Regex("\\*")),
    SLASH(Regex("/")),
    PLUS(Regex("\\+")),
    MINUS(Regex("-")),
    POWER(Regex("\\^")),
    SIN(sinRegex),
    COS(cosRegex),
    TAN(tanRegex),
    LOG(logRegex),
    OPEN_BRACKET(Regex("\\(")),
    CLOSE_BRACKET(Regex("\\)")),
    VAR(Regex("^(?!.*$functionRegex)[a-zA-Z][0-9]*")),
    NUM(Regex("[1-9][0-9]*(.[0-9]+)?")),
    EOF(Regex(""))
}

private val functionRegex = Regex("log|Log|LOG|sin|Sin|SIN|cos|Cos|COS|tan|Tan|TAN")
private val sinRegex = Regex("sin|Sin|SIN")
private val cosRegex = Regex("cos|Cos|COS")
private val tanRegex = Regex("tan|Tan|TAN")
private val logRegex = Regex("log|Log|LOG")


fun main() {
    val str = "x + -y * (-x / - log(10)(x) - 2^alpha)"
    val message = LexiAnalysis().parse(str)
    println(str)
    println(message.first.map { it.name })
    println(message.second)
    val term = SyntacticAnalysis().parse(message.first, message.second)
    println(term)
    println(term.simplify())

    val str1 = "x - x"
    val lex = LexiAnalysis().parse(str1)
    val syn = SyntacticAnalysis().parse(lex.first, lex.second)
    println(syn)
    println(syn.simplify())

}