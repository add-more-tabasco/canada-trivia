/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.canadatrivia

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.android.canadatrivia.databinding.FragmentGameBinding

class GameFragment : Fragment() {
    data class Question(
            val text: String,
            val answers: List<String>)

    // The first answer is the correct one.  We randomize the answers before showing the text.
    // All questions must have four answers.  We'd want these to contain references to string
    // resources so we could internationalize. (or better yet, not define the questions in code...)
    private val questions: MutableList<Question> = mutableListOf(
            Question(text = "What is Canada's national animal?",
                    answers = listOf("the noble beaver", "reindeer", "maple syrup", "Justin Trudeau")),
            Question(text = "Length of Canada's coastline?",
                    answers = listOf("243,042 km", "243 km", "243,042,000 km", "24,304.2 km")),
            Question(text = "Canada's national sport?",
                    answers = listOf("actually Lacrosse", "of course Hockey", "Canadian Football", "ice fishing")),
            Question(text = "Sport invented by a Canadian?",
                    answers = listOf("all of these", "hockey", "lacrosse", "basketball")),
            Question(text = "Canada's largest island is larger than...",
                    answers = listOf("Japan", "Australia", "Indonesia", "all of these")),
            Question(text = "Population density per square km of Canada's largest territory, Nunavut?",
                    answers = listOf("0.02", "2", "20", "0.2")),
            Question(text = "Language spoken natively by ~20% of the population?",
                    answers = listOf("French", "Mandarin", "English", "Classical Greek")),
            Question(text = "Canada has never fought a war against...",
                    answers = listOf("The United Kingdom", "The United States", "South Africa", "Germany")),
            Question(text = "Canada's highest mountain, Mt Logan, is...",
                    answers = listOf("5959m", "4949m", "3939m", "2929m")),
            Question(text = "Canada's population?",
                    answers = listOf("38 Million", "13.8 million", "113.8 million", "3.8 million"))
    )

    lateinit var currentQuestion: Question
    lateinit var answers: MutableList<String>
    private var questionIndex = 0
    private val numQuestions = Math.min((questions.size + 1) / 2, 3)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentGameBinding>(
                inflater, R.layout.fragment_game, container, false)

        // Shuffles the questions and sets the question index to the first question.
        randomizeQuestions()

        // Bind this fragment class to the layout
        binding.game = this

        // Set the onClickListener for the submitButton
        binding.submitButton.setOnClickListener { view: View -> //@Suppress("UNUSED_ANONYMOUS_PARAMETER")
        //{ view: View ->
            val checkedId = binding.questionRadioGroup.checkedRadioButtonId
            // Do nothing if nothing is checked (id == -1)
            if (-1 != checkedId) {
                var answerIndex = 0
                when (checkedId) {
                    R.id.secondAnswerRadioButton -> answerIndex = 1
                    R.id.thirdAnswerRadioButton -> answerIndex = 2
                    R.id.fourthAnswerRadioButton -> answerIndex = 3
                }
                // reset the timer and store its last value

                // The first answer in the original question is always the correct one, so if our
                // answer matches, we have the correct answer.
                if (answers[answerIndex] == currentQuestion.answers[0]) {
                    questionIndex++
                    // Advance to the next question
                    if (questionIndex < numQuestions) {
                        currentQuestion = questions[questionIndex]
                        setQuestion()
                        binding.invalidateAll()
                    } else {
                        // We've won!  Navigate to the gameWonFragment.
                        view.findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameWonFragment(numQuestions, questionIndex))
                    }
                } else {
                    // Game over! A wrong answer sends us to the gameOverFragment.
                    view.findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameOverFragment())
                }
            }
        }
        return binding.root
    }

    // randomize the questions and set the first question
    private fun randomizeQuestions() {
        questions.shuffle()
        questionIndex = 0
        setQuestion()
    }

    // Sets the question and randomizes the answers.  This only changes the data, not the UI.
    // Calling invalidateAll on the FragmentGameBinding updates the data.
    private fun setQuestion() {
        currentQuestion = questions[questionIndex]
        // randomize the answers into a copy of the array
        answers = currentQuestion.answers.toMutableList()
        // and shuffle them
        answers.shuffle()
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_canada_trivia_question, questionIndex + 1, numQuestions)
    }
}
