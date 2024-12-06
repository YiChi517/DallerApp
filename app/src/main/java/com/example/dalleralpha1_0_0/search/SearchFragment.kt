package com.example.dalleralpha1_0_0.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.example.dalleralpha1_0_0.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException

class SearchFragment : Fragment() {
    private lateinit var chatContainer: LinearLayout
    private lateinit var chatScrollView: ScrollView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button

    private val client = OkHttpClient() // OkHttp 客戶端
    private val groqEndpoint = "https://api.groq.com/openai/v1/chat/completions"
    private val groqApiKey = "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" // 替換為您的 API 金鑰
    private var promptText: String = "" // 保存 prompt 內容

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        chatContainer = view.findViewById(R.id.chatContainer)
        chatScrollView = view.findViewById(R.id.chatScrollView)
        messageInput = view.findViewById(R.id.messageInput)
        sendButton = view.findViewById(R.id.sendButton)

        promptText = readPromptFile()

        // 添加初始歡迎語
        addAiMessage("你好，我是小達~今天想要學習什麼金融知識呢？")

        sendButton.setOnClickListener {
            val userMessage = messageInput.text.toString()
            if (userMessage.isNotEmpty()) {
                addUserMessage(userMessage)
                messageInput.text.clear()
                sendToGroq(userMessage)
            }
        }

        return view
    }

    private fun readPromptFile(): String {
        return try {
            val file = File(requireContext().filesDir, "prompt/prompt.txt")
            file.readText()
        } catch (e: Exception) {
            "預設的提示文本未找到。"
        }
    }

    private fun addUserMessage(message: String) {
        val userMessageView = TextView(context).apply {
            text = message
            setBackgroundResource(R.drawable.user_bubble)
            setTextColor(resources.getColor(android.R.color.black))
            textSize = 14f
            setPadding(24, 16, 24, 16)
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(50, 8, 16, 8)
        }
        userMessageView.layoutParams = params

        val userMessageLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.END
            addView(userMessageView)
        }

        chatContainer.addView(userMessageLayout)
        scrollToBottom()
    }

    private fun addAiMessage(message: String) {
        val aiMessageView = TextView(context).apply {
            text = message
            setBackgroundResource(R.drawable.ai_bubble)
            setTextColor(resources.getColor(android.R.color.black))
            textSize = 14f
            setPadding(24, 16, 24, 16)
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(16, 8, 50, 8)
        }
        aiMessageView.layoutParams = params

        val aiMessageLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.START
            addView(aiMessageView)
        }

        chatContainer.addView(aiMessageLayout)
        scrollToBottom()
    }

    private fun scrollToBottom() {
        chatScrollView.post {
            chatScrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    private fun sendToGroq(userMessage: String) {
        val messages = JSONArray().apply {
            put(JSONObject().apply {
                put("role", "system")
                put("content", promptText)
            })
            put(JSONObject().apply {
                put("role", "user")
                put("content", userMessage)
            })
        }

        val requestBody = JSONObject().apply {
            put("messages", messages)
            put("model", "llama-3.2-90b-vision-preview")
            put("temperature", 1)
            put("max_tokens", 1024)
            put("top_p", 1)
            put("stream", false)
            put("stop", JSONObject.NULL)
        }

        val body = RequestBody.create(
            MediaType.parse("application/json"),
            requestBody.toString()
        )

        val request = Request.Builder()
            .url(groqEndpoint)
            .addHeader("Authorization", "Bearer $groqApiKey")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    addAiMessage("Error: 無法連接到 AI 伺服器。")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    activity?.runOnUiThread {
                        addAiMessage("Error: 無法從 AI 獲取有效回應。")
                    }
                    return
                }

                response.body()?.string()?.let { responseBody ->
                    val aiMessage = try {
                        JSONObject(responseBody).getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")
                    } catch (e: Exception) {
                        "Error: 無法解析 AI 回應。"
                    }
                    activity?.runOnUiThread {
                        addAiMessage(aiMessage)
                    }
                }
            }
        })
    }
}
