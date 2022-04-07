package analysis.terms.parsing

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
                        val last = tokenList.lastOrNull()
                        val token = if (last == Token.CLOSE_BRACKET || last == Token.NUM || last == Token.VAR) {
                            Token.PLUS
                        } else {
                            Token.POSITIVE
                        }
                        tokenList.add(token)
                        current += delta
                        delta = strWithoutSpaces.length - current
                    } else if (tokens.contains(Token.NEGATIVE)&& tokens.contains(Token.MINUS)) {
                        val last = tokenList.lastOrNull()
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