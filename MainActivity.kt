package com.example.filevault

import android.app.Activity import android.content.Intent import android.net.Uri import android.os.Bundle import android.os.Environment import android.widget.* import androidx.appcompat.app.AppCompatActivity import java.io.* import java.security.SecureRandom import javax.crypto.* import javax.crypto.spec.GCMParameterSpec import javax.crypto.spec.PBEKeySpec import javax.crypto.spec.SecretKeySpec import java.security.spec.KeySpec import javax.crypto.SecretKeyFactory

class MainActivity : AppCompatActivity() {

private val PICK_FILE_REQUEST = 1
private lateinit var passwordInput: EditText
private lateinit var selectButton: Button
private lateinit var infoText: TextView
private lateinit var secretKey: SecretKeySpec
private val saltFileName = "vault_salt"

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val layout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(20, 50, 20, 50)
    }

    passwordInput = EditText(this).apply {
        hint = "Enter vault password"
    }
    selectButton = Button(this).apply {
        text = "Select File to Encrypt"
    }
    infoText = TextView(this)

    layout.addView(passwordInput)
    layout.addView(selectButton)
    layout.addView(infoText)

    setContentView(layout)

    selectButton.setOnClickListener {
        val password = passwordInput.text.toString().toCharArray()
        if (password.isEmpty()) {
            Toast.makeText(this, "Password required", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }
        val salt = getOrCreateSalt()
        secretKey = deriveKey(password, salt)
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
        }
        startActivityForResult(intent, PICK_FILE_REQUEST)
    }
}

private fun getOrCreateSalt(): ByteArray {
    val file = File(filesDir, saltFileName)
    return if (file.exists()) {
        file.readBytes()
    } else {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        file.writeBytes(salt)
        salt
    }
}

private fun deriveKey(password: CharArray, salt: ByteArray): SecretKeySpec {
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    val spec: KeySpec = PBEKeySpec(password, salt, 65536, 256)
    val tmp = factory.generateSecret(spec)
    return SecretKeySpec(tmp.encoded, "AES")
}

private fun encryptFile(inputStream: InputStream, outputFile: File, key: SecretKeySpec) {
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }
    val gcmSpec