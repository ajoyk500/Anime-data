package com.akcreation.gitsilent.git

import android.content.Context
import com.jcraft.jsch.JSch
import com.jcraft.jsch.KeyPair
import org.eclipse.jgit.api.TransportConfigCallback
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.transport.Transport
import java.io.File
import java.io.FileOutputStream

/**
 * Version 1.1 Feature: SSH Key Authentication
 * Manages SSH keys for Git operations
 */
class SSHKeyManager(private val context: Context) {
    
    private val sshDir = File(context.filesDir, ".ssh")
    private val privateKeyFile = File(sshDir, "id_rsa")
    private val publicKeyFile = File(sshDir, "id_rsa.pub")
    private val knownHostsFile = File(sshDir, "known_hosts")
    
    init {
        if (!sshDir.exists()) {
            sshDir.mkdirs()
        }
    }
    
    data class SSHKeyPair(
        val privateKey: String,
        val publicKey: String,
        val fingerprint: String
    )
    
    /**
     * Generate new SSH key pair (RSA 4096-bit)
     */
    fun generateKeyPair(passphrase: String = ""): SSHKeyPair {
        val jsch = JSch()
        val keyPair = KeyPair.genKeyPair(jsch, KeyPair.RSA, 4096)
        
        // Save private key
        val privateKeyOutput = FileOutputStream(privateKeyFile)
        if (passphrase.isNotEmpty()) {
            keyPair.writePrivateKey(privateKeyOutput, passphrase.toByteArray())
        } else {
            keyPair.writePrivateKey(privateKeyOutput)
        }
        privateKeyOutput.close()
        
        // Save public key
        val publicKeyOutput = FileOutputStream(publicKeyFile)
        keyPair.writePublicKey(publicKeyOutput, "gitsilent@android")
        publicKeyOutput.close()
        
        // Get fingerprint
        val fingerprint = keyPair.getFingerPrint()
        
        // Read keys as strings
        val privateKey = privateKeyFile.readText()
        val publicKey = publicKeyFile.readText()
        
        keyPair.dispose()
        
        return SSHKeyPair(privateKey, publicKey, fingerprint)
    }
    
    /**
     * Import existing SSH key
     */
    fun importKey(privateKey: String, publicKey: String): Boolean {
        return try {
            privateKeyFile.writeText(privateKey)
            publicKeyFile.writeText(publicKey)
            privateKeyFile.setReadable(true, true)
            privateKeyFile.setWritable(true, true)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Check if SSH key exists
     */
    fun hasSSHKey(): Boolean {
        return privateKeyFile.exists() && publicKeyFile.exists()
    }
    
    /**
     * Get public key content
     */
    fun getPublicKey(): String? {
        return if (publicKeyFile.exists()) {
            publicKeyFile.readText()
        } else null
    }
    
    /**
     * Get SSH key fingerprint
     */
    fun getKeyFingerprint(): String? {
        if (!privateKeyFile.exists()) return null
        
        return try {
            val jsch = JSch()
            val keyPair = KeyPair.load(jsch, privateKeyFile.absolutePath)
            val fingerprint = keyPair.getFingerPrint()
            keyPair.dispose()
            fingerprint
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Delete SSH keys
     */
    fun deleteKeys(): Boolean {
        var success = true
        if (privateKeyFile.exists()) success = success && privateKeyFile.delete()
        if (publicKeyFile.exists()) success = success && publicKeyFile.delete()
        return success
    }
    
    /**
     * Create SSH transport config for JGit
     * Note: This is a simplified version. For full SSH support,
     * use HTTPS with tokens instead as it's more reliable on Android.
     */
    fun createSSHTransportConfig(passphrase: String = ""): TransportConfigCallback {
        return TransportConfigCallback { transport: Transport ->
            if (transport is SshTransport) {
                // SSH support requires additional JGit SSH implementation
                // For production use, prefer HTTPS authentication
            }
        }
    }
    
    /**
     * Test SSH connection
     */
    suspend fun testSSHConnection(host: String): Boolean {
        return try {
            val jsch = JSch()
            jsch.addIdentity(privateKeyFile.absolutePath)
            
            val session = jsch.getSession("git", host, 22)
            session.setConfig("StrictHostKeyChecking", "no")
            session.connect(5000)
            val connected = session.isConnected
            session.disconnect()
            connected
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Add host to known_hosts
     */
    fun addKnownHost(host: String, key: String) {
        val entry = "$host $key\n"
        knownHostsFile.appendText(entry)
    }
    
    /**
     * Check if using SSH URL
     */
    fun isSSHUrl(url: String): Boolean {
        return url.startsWith("git@") || url.startsWith("ssh://")
    }
}