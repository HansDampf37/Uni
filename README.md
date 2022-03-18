### Simplifying mathematical expressions
the simplifier has to be able to perform numerical operations and to flatten the structure.
when simplifying a term he does the following:
1. flatten the structure t -> t
2. apply all matching rules t -> {t1, ..., tn}
3. perform numerical operations {t1, ..., tn} -> {t1, ..., tn}
4. calculate the Güte of each Term {t1, ..., tn} -> {g1, ..., gn}
5. take the k terms with the highest güte
6. for each of the k terms do 1. - 4. -> {{t1, ..., tn}1, ... {t1, ..., tn}k}
7. calculate the güte of each term {{g1, ..., gn}1, ... {g1, ..., gn}k}
8. repeat from 5

the higher k the more likely and the slower it is to get simple expressions

### Güte of a term
the güte describes how "good" a term is. Be t the term-tree with 
- t.depth = longest path in t,
- t.width = amount of leafs in t,
- t.size = amount of nodes in t with
- a, b in [0, 1] a+b < 1 

one could define the güte as:

`-(a * t.depth + b * t.width + (1 - a - b) * t.size)`
