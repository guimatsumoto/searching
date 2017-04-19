# The_Search

No package com.thesearch.dictionary_manager eu to colocando o arquivo de dicionário
em ingles e uma classe generica que vai ser utilizada pra armazenar as palavras dos
dicionários que a gente vai ter (possivelmente mais de uma lingua, ingles e frances
talvez)

O english_dictionary.txt eu criei usando o scowl, que é um arquivo de palavras em ingles
separadas por tamanho, classificação e uso (nos EUA, Inglaterra, Canada, Austrália...).
Criei ele com o comando ./mk-list -v3 american 95
O que significa que estou pegando todas as palavras em inglês, com suas variações pro 
inglês americano, de tamanho até 95 (que é o maior tamanho, então pega todas as palavras)
e com as variações de até terceiro nivel (-v3, acho que aqui incluem conjugações e afins)

Utilisamos a biblioteca externa Jsoup para facilitar a extracao de elementos HTML. Essa
extracao eh utilizada para buscar a sugestao de correcao oferecida pelo google.