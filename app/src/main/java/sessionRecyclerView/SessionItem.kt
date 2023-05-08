package sessionRecyclerView

class SessionItem(private val pocetSerii: Int, private val pocetOpakovani: Int, private val vaha: Int) {

    fun getFullWeight(): Int {
        return vaha*pocetOpakovani*pocetSerii
    }

    fun getTextRepresentation(): String {
        return pocetSerii.toString() + "x" + pocetOpakovani.toString() + "x" + vaha.toString() + "kg"
    }
}