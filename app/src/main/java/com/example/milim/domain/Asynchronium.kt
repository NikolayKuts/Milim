package com.example.milim.domain

class Asynchronium<G, R> {
    private var response: R? = null

    fun execute(given: G, inBackground: (G) -> R) { // add "inline" !!!
        val thread =
            Thread(object : Runnable { //   val thread = Thread { TODO("Not yet implemented") }
                override fun run() {
                    response = inBackground(given)
                }
            })
        thread.start()
        thread.join()
    }

        fun getResponse(): R? {
        return response// ?: Any() as R
    }
}

