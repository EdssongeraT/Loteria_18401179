package com.example.loteria_18401179

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.loteria_18401179.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    var indices = Array<Int>(54) { i -> i + 1 }
    var j=juego(this,indices)
    lateinit var mp: MediaPlayer
    var indice=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.terminar.isEnabled=false
        binding.pausar.isEnabled=false

        binding.barajear.setOnClickListener{
                indices.shuffle()

        }

        binding.iniciar.setOnClickListener{
            //barajeado inicial para que no siempre empiece en orden
            indices.shuffle()
            //si el juego anterior sigue corriendo en la fase de comprobacion
            //el boton iniciar solo mandara un mensaje que diga espera
            if(j.mazoCompleto) {
                setTitle("juego en curso")

                j.mazoCompleto = false
                mp = MediaPlayer.create(this, R.raw.inicio)
                binding.iniciar.isEnabled = false
                binding.barajear.isEnabled = false
                binding.pausar.isEnabled = true
                binding.terminar.isEnabled = true
                mp.start()
                if (!j.ejecutar) {
                    j.i = 0
                    j.ejecutar = true

                } else if (j.ejecutar) {
                    j.start()
                    j.dormir(3000)
                }
            }else{
                    setTitle("espera a que termine el juego anterior")
            }
        }

        binding.pausar.setOnClickListener{
            if (j.estaPausado()){
                j.despausarHilo()
                binding.pausar.setText("pausar juego")
            }else if(!j.estaPausado()){
                j.pausarHIlo()
                binding.pausar.setText("reanudar juego")
            }
        }

        binding.terminar.setOnClickListener {
            setTitle("comprobando cartas")
            binding.pausar.isEnabled = false
            binding.terminar.isEnabled = false
            j.terminarHilo()
            if (j.mazoCompleto) {
                mp=MediaPlayer.create(this, R.raw.completo)
                activar()
                mp.start()

            } else {
                indice = j.i + 1
                binding.barajear.isEnabled = false
                mp = MediaPlayer.create(this, R.raw.fin)
                mp.start()
                mp.setOnCompletionListener { mp.release() }


                val corutina = GlobalScope.launch {
                    delay(5000)
                    while (indice < indices.size) {

                        var mpl=MediaPlayer.create(this@MainActivity, audio(indice))
                        this@MainActivity.runOnUiThread {
                            binding.imprimir.setText("${indice}")
                            binding.mazo.setBackgroundResource(imagen(indice))
                        }
                        mpl.start()
                        mpl.setOnCompletionListener { mp.release() }
                        delay(3000)
                        indice++
                        if(indice==indices.size-1){
                            j.mazoCompleto =true
                        }
                    }
                }

                activar()
            }
        }
    }

    fun activar(){
        binding.barajear.isEnabled=true
        binding.iniciar.isEnabled=true
    }

    fun imagen(id: Int): Int {
        var nombre = "carta${indices[id]}"
        return resources.getIdentifier(nombre, "drawable", packageName)
    }

    fun audio(id: Int): Int {
        var nombre = "a${indices[id]}"
        return resources.getIdentifier(nombre, "raw", packageName)
    }
}

