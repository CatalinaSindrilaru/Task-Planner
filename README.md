Sindrilaru Catalina-Maria
Grupa: 332CA

               ALGORITMI PARALELI SI DISTRIBUITI
       Tema #2 Planificarea de task-uri ı̂ntr-un datacenter

---------------------------MyDispatcher---------------------------

In cadrul acestei teme, am avut de implementat functionalitatile
dispatcherului si a hosturilor. Astfel, am observat ca dispatcherul
primeste task-uri de la mai multe threaduri simultan, fapt pentru 
care am folosit 'synchronized' pentru metoda addTask. In addTask,
am tratat cele 4 cazuri. Pentru Round Robin, am folosit o functie
auxiliara care aplica formula mentionata si in enunt id = (i+1) % n,
dar eu am id = (id + 1) % n, unde n este numarul de hosturi. Apoi,
doar am asignat taskul primit hostului cu id-ul returnat de functie.
Urmatorul algoritm tratat este Size_Interval_Task_Assignment, pentru
care doar am verificat daca tipul unui task este scurt, mediu sau lung
si le-am asignat catre hosturile 0, 1 si respectiv 2. 
Shortest Queue si Least Work Left le-am tratat aproape identic, am
plecat de la un minim egal cu MAX_VALUE si apoi am trecut prin toate
hosturile, comparand dupa caz acel minim cu dimensiunea cozii sau cu
timpul ramas de rulare (timpul intors de host este in milisecunde,
asa ca l-am convertit in secunde si apoi l-am rotunjit, asa cum mentiona
in enunt) si am salvat id-ul hostului cu cea mai mica coada
timp ramas de rulat. Daca la un moment dat cozile sau timpul de rulare
a doua hosturi era egal, l-am ales pe cel cu id-ul mai mic.


---------------------------MyHost-------------------------------------

Pentru a implementa coada unui host, asa cum se mentiona in cerinta, am
ales sa folosesc o coada de tip 'PriorityBlockingQueue' pentru a nu exista
probleme de concurenta si pentru ca aceasta imi permite sa sortez
taskurile in functie de prioritate si timpul de start, asa cum imi doresc.
Pentru a opri executia unui host, am folosit o variabila ce intial este
true, dar care in metoda shutdown devine false. In metoda
addTask, am adaugat noul task venit in coada si am verificat daca exista
un task deja in rulare, care este preemtibil si fata de care noul task
are prioritate mai mare. Daca acest lucru se intampla, voi intrerupe
executia acestuia. In functia run, daca nu am un task in executie, iau
primul task din coada (care este cel cu prioritatea cea mai buna, avand in
vedere algoritmul de sortare al cozii) si apelez metoda sleep chiar
pe durata de executie a acelui task. Daca se ajunge la sfarsitul duratei
de executie fara o intrerupere, apelez finish pe task si il
scot din coada, iar task-ul curent in setez pe null pentru a-mi permite
scoaterea urmatorului task din coada. Daca exista o intrerupere, calculez 
timpul pe care task-ul anterior l-a petrecut in sleep si ii
actualizez timpul ramas de rulat. Daca acesta insa si-a terminat
executia, ii dau finish si il scot din coada.
