package nonmain

class MehoDaoImpl(private val a: String, private val b: Int) : MehoDao {

    override fun nesto() {
        println(a)
    }

    override fun josNesto() {
        print(b)
    }
}