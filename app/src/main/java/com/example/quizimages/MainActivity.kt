package com.example.quizimages
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


data class Question(
    val texte: String,
    val image: Int,
    val choix: Array<String>,
    val bonneReponse: Int,
    val description: String
)

class MainActivity : AppCompatActivity() {

    val questions = arrayOf(
        Question(
            "Quelle est cette forme ?",
            R.drawable.triangle,
            arrayOf("Carré", "Triangle", "Cercle"),
            1,
            "Une forme orange à trois côtés"
        ),
        Question(
            "Combien de côtés possède cette forme ?",
            R.drawable.carre,
            arrayOf("4", "3", "5"),
            0,
            "Une forme bleue à quatre côtés égaux"
        ),
        Question(
            "Quelle est cette forme ?",
            R.drawable.cercle,
            arrayOf("Triangle", "Rectangle", "Cercle"),
            2,
            "Une forme verte ronde"
        ),
        Question(
            "Combien de pointes possède cette étoile ?",
            R.drawable.etoile,
            arrayOf("3", "5", "6"),
            1,
            "Une étoile violette à cinq pointes"
        ),
        Question(
            "Quelle est cette forme ?",
            R.drawable.rectangle,
            arrayOf("Rectangle", "Cercle", "Triangle"),
            0,
            "Une forme rouge allongée à quatre angles droits"
        )
    )

    var numeroQuestion = 0
    var score = 0
    var nombreReponses = 0
    var dejaRepondu = false
    var partieTerminee = false

    lateinit var txtScore: TextView
    lateinit var txtNumero: TextView
    lateinit var txtQuestion: TextView
    lateinit var txtRetour: TextView
    lateinit var txtResultat: TextView

    lateinit var imgQuestion: ImageView
    lateinit var progressQuiz: ProgressBar
    lateinit var scrollContenu: ScrollView

    lateinit var btnChoix1: Button
    lateinit var btnChoix2: Button
    lateinit var btnChoix3: Button
    lateinit var btnSuivant: Button
    lateinit var btnRejouer: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtScore = findViewById(R.id.txt_score)
        txtNumero = findViewById(R.id.txt_numero)
        txtQuestion = findViewById(R.id.txt_question)
        txtRetour = findViewById(R.id.txt_retour)
        txtResultat = findViewById(R.id.txt_resultat)

        imgQuestion = findViewById(R.id.img_question)
        progressQuiz = findViewById(R.id.progress_quiz)
        scrollContenu = findViewById(R.id.scroll_contenu)

        btnChoix1 = findViewById(R.id.btn_choix1)
        btnChoix2 = findViewById(R.id.btn_choix2)
        btnChoix3 = findViewById(R.id.btn_choix3)
        btnSuivant = findViewById(R.id.btn_suivant)
        btnRejouer = findViewById(R.id.btn_rejouer)

        val racine = txtScore.parent.parent as View
        val marge = racine.paddingLeft

        ViewCompat.setOnApplyWindowInsetsListener(racine) { vue, insets ->
            val barres = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            vue.setPadding(
                marge + barres.left,
                marge + barres.top,
                marge + barres.right,
                marge + barres.bottom
            )

            insets
        }
        ViewCompat.requestApplyInsets(racine)


        btnChoix1.setOnClickListener {
            verifierReponse(0)
        }

        btnChoix2.setOnClickListener {
            verifierReponse(1)
        }

        btnChoix3.setOnClickListener {
            verifierReponse(2)
        }

        btnSuivant.setOnClickListener {
            questionSuivante()
        }

        btnRejouer.setOnClickListener {
            rejouer()
        }

        afficherQuestion()
    }

    fun afficherQuestion() {
        val question = questions[numeroQuestion]

        txtNumero.text = "Question ${numeroQuestion + 1} sur 5"
        txtQuestion.text = question.texte

        imgQuestion.setImageResource(question.image)
        imgQuestion.contentDescription = question.description

        btnChoix1.text = question.choix[0]
        btnChoix2.text = question.choix[1]
        btnChoix3.text = question.choix[2]

        // Autoriser une réponse pour cette nouvelle question.
        dejaRepondu = false

        btnChoix1.isEnabled = true
        btnChoix2.isEnabled = true
        btnChoix3.isEnabled = true

        txtRetour.text = ""
        btnSuivant.visibility = View.GONE

        actualiserScoreEtProgression()
    }

    fun verifierReponse(choix: Int) {
        // Ne jamais compter deux réponses pour une question.
        if (dejaRepondu || partieTerminee) {
            return
        }

        dejaRepondu = true

        val question = questions[numeroQuestion]

        if (choix == question.bonneReponse) {
            score = score + 1
            txtRetour.text = "Bonne réponse !"
        } else {
            val bonneReponse = question.choix[question.bonneReponse]
            txtRetour.text =
                "Mauvaise réponse. La bonne réponse est : $bonneReponse."
        }

        // Bonne ou mauvaise : la progression avance.
        nombreReponses = nombreReponses + 1

        actualiserScoreEtProgression()

        btnChoix1.isEnabled = false
        btnChoix2.isEnabled = false
        btnChoix3.isEnabled = false

        if (numeroQuestion == questions.lastIndex) {
            btnSuivant.text = "Voir le résultat"
        } else {
            btnSuivant.text = "Question suivante"
        }

        btnSuivant.visibility = View.VISIBLE
    }

    fun actualiserScoreEtProgression() {
        txtScore.text = "Score : $score/5"
        progressQuiz.progress = nombreReponses
        progressQuiz.contentDescription =
            "$nombreReponses réponses données sur 5"
    }

    fun questionSuivante() {
        // Impossible d'avancer sans avoir répondu.
        if (!dejaRepondu || partieTerminee) {
            return
        }

        if (numeroQuestion < questions.lastIndex) {
            numeroQuestion = numeroQuestion + 1
            afficherQuestion()
        } else {
            afficherResultat()
        }

        scrollContenu.scrollTo(0, 0)
    }

    fun afficherResultat() {
        partieTerminee = true

        // Masquer les éléments du quiz.
        txtNumero.visibility = View.GONE
        imgQuestion.visibility = View.GONE
        txtQuestion.visibility = View.GONE
        btnChoix1.visibility = View.GONE
        btnChoix2.visibility = View.GONE
        btnChoix3.visibility = View.GONE
        txtRetour.visibility = View.GONE
        btnSuivant.visibility = View.GONE

        // Afficher le résultat dans la même activité.
        txtResultat.text = "Quiz terminé !\nTon score : $score/5"
        txtResultat.visibility = View.VISIBLE
        btnRejouer.visibility = View.VISIBLE
    }

    fun rejouer() {
        // Remettre les variables à zéro.
        numeroQuestion = 0
        score = 0
        nombreReponses = 0
        dejaRepondu = false
        partieTerminee = false

        txtResultat.visibility = View.GONE
        btnRejouer.visibility = View.GONE

        txtNumero.visibility = View.VISIBLE
        imgQuestion.visibility = View.VISIBLE
        txtQuestion.visibility = View.VISIBLE
        btnChoix1.visibility = View.VISIBLE
        btnChoix2.visibility = View.VISIBLE
        btnChoix3.visibility = View.VISIBLE
        txtRetour.visibility = View.VISIBLE

        afficherQuestion()
        scrollContenu.scrollTo(0, 0)
    }
}