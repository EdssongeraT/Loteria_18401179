package com.example.loteria_18401179

import android.media.MediaPlayer

class juego(activity: MainActivity,indices:Array<Int>):Thread(){
    var act = activity
    val indices = indices
    var ejecutar = true
    private var pausar = false
    var i=0
    var mazoCompleto = true

    override fun run() {
        super.run()
        mazoCompleto = false

            while (i < indices.size) {

                if (ejecutar) {
                    if (!estaPausado()) {

                        dormir(3000)
                        act.runOnUiThread {
                            var mediaPlayer = MediaPlayer.create(act, act.audio(i))
                            act.binding.mazo.setBackgroundResource(act.imagen(i))
                            act.binding.imprimir.setText("${i}")
                            mediaPlayer.start()
                            mediaPlayer.setOnCompletionListener {
                                mediaPlayer.release()
                            }
                        }

                        i++
                    }
                }
                if(i==indices.size-1){
                    mazoCompleto = true
                    ejecutar=false
                }
            }
    }
    fun dormir(ti:Long){
        sleep(ti)
    }
    fun terminarHilo(){
        ejecutar = false
    }

    fun pausarHIlo(){
        pausar = true
    }

    fun despausarHilo(){
        pausar = false
    }

    fun estaPausado(): Boolean {
        return pausar
    }
}