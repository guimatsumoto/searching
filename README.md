#########################################################################
# The_Search                                                            #
#                                                                       #
# Authors: MATSUMOTO Guilherme and PETRY Gabriel                        #
#                                                                       #
#########################################################################

Ce repositoire git contient les derniers modifications qu'on a fait sur le logiciel. La pluplart des modifications peuvent étre touvé
sur le repositoire The_Search, qui à cause d'une mal practice du git, a été "rollback" à une version ancienne. On a crée le repositoire
"searching" parce que la résolution de conflits sur "The_Search" était trop couteause.

Pour bien compiler ce projet, on a utilisé l'IDE IntelliJ de JetBrains. Il faut avoir au moins au moins java 1.7 et utiliser le niveau
de langage au moins 7.0.

Une fois le projet est ouverte sur le logiciel, vous pouvez changer ces configurations sur "Project", dans "Project Settings". Là bas c'est
possible de changer la version java sur "Project SDK" et le niveau de langage sur "Project Language Level".

L'archive à éxécuter est le searchGui.java, qui contient le vrai main(). Attention, parce que on a laissé une autre version de main, qui
n'utilise pas des ressources visuels (c'est juste un appli terminal), pour faire des tests initiales.

Au lieu de tout recompiler, vue que ça prend du temps, on vous conseille d'utiliser la version de production, déjà compilé et prêt à
éxécution. Pour cela il faut lancer un terminal et chercher le dossier "/out/artifacts/the_search_jar". Une fois dedans ce dossier il
faut lancer le command terminal "java -jar the_search.jar" et le programme sera lancé. Il faut néanmoins avoir java 1.7 ou supérieur.