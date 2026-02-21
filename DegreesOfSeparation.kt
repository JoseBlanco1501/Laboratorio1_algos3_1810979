import java.io.File

interface Grafo<T> {
    fun agregarVertice(v: T): Boolean
    fun conectar(desde: T, hasta: T): Boolean
    fun contiene(v: T): Boolean
    fun obtenerArcosSalida(v: T): List<T>
}

/**
 * Implementacion de un grafo dirigido utilizando listas de adyacencia.
 * Cada vertice almacena una lista de sus arcos de salida.
 *
 * @param T tipo generico de los vertices almacenados en el grafo
 */
class ListaAdyacenciaGrafo<T> : Grafo<T> {

    /**
     * Mapa interno que almacena cada vertice junto con su lista
     * de vertices adyacentes (arcos de salida).
     */
    private val adyacencia = mutableMapOf<T, MutableList<T>>()

    /**
     * Agrega un nuevo vertice al grafo si no existe previamente.
     *
     * @param v vertice a agregar
     * @return true si el vertice fue agregado, false si ya existia
     */
    override fun agregarVertice(v: T): Boolean {
        if (v in adyacencia) return false
        adyacencia[v] = mutableListOf()
        return true
    }

    /**
     * Crea un arco dirigido desde un vertice hacia otro.
     *
     * @param desde vertice origen
     * @param hasta vertice destino
     * @return true si ambos vertices existen y el arco fue creado
     */
    override fun conectar(desde: T, hasta: T): Boolean {
        if (desde !in adyacencia || hasta !in adyacencia) return false
        adyacencia[desde]!!.add(hasta)
        return true
    }

    /**
     * Verifica si un vertice existe en el grafo.
     *
     * @param v vertice a verificar
     * @return true si el vertice existe
     */
    override fun contiene(v: T): Boolean = v in adyacencia

    /**
     * Devuelve la lista de arcos de salida de un vertice.
     * Se retorna una copia inmutable para evitar modificaciones externas.
     *
     * @param v vertice a consultar
     * @return lista de vertices adyacentes o una lista vacia
     */
    override fun obtenerArcosSalida(v: T): List<T> {
        return adyacencia[v]?.toList() ?: emptyList()
    }

    /**
     * Calcula el grado de separacion entre dos vertices utilizando
     * el algoritmo de busqueda en anchura (BFS).
     *
     * El grado de separacion se define como la cantidad minima de arcos
     * que deben recorrerse para ir desde el vertice `inicio` hasta el vertice `fin`.
     *
     * Reglas:
     * - Si inicio y fin son el mismo vertice, el grado es 0.
     * - Si alguno de los vertices no existe en el grafo, se retorna -1.
     * - Si no existe un camino entre ambos vertices, se retorna -1.
     *
     * Este metodo asume que el grafo puede ser dirigido o no dirigido,
     * dependiendo de como se hayan agregado los arcos.
     *
     * @param inicio vertice origen desde donde comienza la busqueda
     * @param fin vertice destino al cual se desea llegar
     * @return el grado de separacion entre ambos vertices, o -1 si no existe conexion
     */
    fun degreesOfSeparation(inicio: T, fin: T): Int {

        if (inicio == fin) return 0
        if (!contiene(inicio) || !contiene(fin)) return -1

        val visitados = mutableSetOf<T>()   // Conjunto de vertices ya visitados para evitar ciclos
        val distancia = mutableMapOf<T, Int>()  // Mapa que almacena la distancia (grado) desde el vertice 

        // Cola FIFO utilizada por BFS
        val cola = ArrayDeque<T>()

        // Inicialización del BFS
        cola.addLast(inicio)
        visitados.add(inicio)
        distancia[inicio] = 0

        // Bucle principal del BFS
        while (cola.isNotEmpty()) {

            // Se toma el siguiente vertice a procesar
            val actual = cola.removeFirst()
            val distActual = distancia[actual]!!

            // Se recorren todos los vecinos del vértice actual
            for (vecino in obtenerArcosSalida(actual)) {

                // Si el vecino aun no ha sido visitado, se procesa
                if (vecino !in visitados) {
                    visitados.add(vecino)
                    distancia[vecino] = distActual + 1
                    cola.addLast(vecino)

                    // Si llegamos al destino, retornamos inmediatamente
                    if (vecino == fin) {
                        return distancia[vecino]!!
                    }
                }
            }
        }

        return -1   // Si BFS termina sin encontrar el destino, no existe conexion
    }

}

/**
 * Programa principal que calcula el grado de separacion entre dos personas
 * utilizando un grafo construido a partir del archivo input.txt.
 *
 * El archivo debe contener pares de nombres separados por un espacio,
 *
 * Uso esperado:
 *   java -jar DegreesOfSeparation.jar <persona1> <persona2>
 *
 * Salida:
 * - Un numero entero indicando el grado de separacion
 * - 0 si ambas personas son la misma
 * - -1 si no existe conexion entre ellas
 */
fun main(args: Array<String>) {

    // Validacion de argumentos
    if (args.size != 2) {
        println("Uso: java -jar DegreesOfSeparation.jar <persona1> <persona2>")
        return
    }

    val origen = args[0]
    val destino = args[1]

    // Grafo donde se almacenaran las relaciones de amistad
    val grafo = ListaAdyacenciaGrafo<String>()

    // Lectura del archivo input.txt
    File("input.txt").forEachLine { linea ->
        val nombres = linea.split(" ")
        if (nombres.size == 2) {
            val a = nombres[0]
            val b = nombres[1]

            // Se agregan los vertices si no existen
            grafo.agregarVertice(a)
            grafo.agregarVertice(b)

            // La amistad es bidireccional
            grafo.conectar(a, b)
            grafo.conectar(b, a)
        }
    }

    // Calculo del grado de separacion
    val resultado = grafo.degreesOfSeparation(origen, destino)
    println(resultado)
}


