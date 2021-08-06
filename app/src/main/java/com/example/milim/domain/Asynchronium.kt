package com.example.milim.domain

class Asynchronium<G, R> {
    private var response: R? = null

    fun execute(given: G, inBackground: (G) -> R) {
            Thread { response = inBackground(given) }.also {
                it.start()
                it.join()
            }
    }

        fun getResponse(): R? {
        return response
    }
}

