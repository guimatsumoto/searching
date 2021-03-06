#########################################################################
# The_Search                                                            #
#                                                                       #
# Authors: MATSUMOTO Guilherme and PETRY Gabriel                        #
#                                                                       #
#########################################################################

Ce repositoire git contient les dernières modifications qu'on a fait sur le logiciel. La pluplart des modifications
 peuvent être trouvées sur le repositoire "The_Search", qui à cause d'un problème avec git a été "rollback" à une
 version ancienne. On a crée le repositoire "searching" parce que la résolution de conflits sur "The_Search" était
trop couteuse.

Pour bien compiler ce projet, on a utilisé l'IDE IntelliJ de JetBrains. Il faut avoir au moins java 1.7 (1.8 recomendée)
et utiliser le niveau de langage au moins 7.0.

Une fois que le projet est ouvert sur le logiciel, vous pouvez changer ces configurations dans
"Project" -> "Project Settings". Là-bas, c'est possible de changer la version java sur "Project SDK" et le niveau de
langage sur "Project Language Level".

L'archive à exécuter c'est searchGui.java, qui contient le vrai main(). Attention, parce que on a laissé une autre
version de main, qui n'utilise pas des ressources visuels (c'est juste un appli terminal), pour faire des tests
initiales.

################ IMPORTANT #######################
#
# Au lieu de tout recompiler, on vous conseille d'utiliser la version de production, déjà compilé et prêt à éxécution.
# Pour cela il faut lancer un terminal et chercher le dossier "/out/artifacts/the_search_jar". Une fois dans ce dossier
# il faut lancer le command terminal "java -jar the_search.jar" et le programme sera lancé. Il faut néanmoins avoir
# java 1.7 ou supérieur (1.8 recomendée).
#
##################################################

Tous les commentaires sur le code ont été faits en anglais, parce qu'on était habitué à les faire de cette façon.