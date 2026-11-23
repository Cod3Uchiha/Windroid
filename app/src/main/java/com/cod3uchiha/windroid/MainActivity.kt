package com.cod3uchiha.windroid

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WindroidHome(
                        report = remember { HostProbe.inspect(this) },
                        onSetup = { /* VM engine integration is the next implementation stage. */ }
                    )
                }
            }
        }
    }
}

private data class HostReport(
    val cpuAbi: String,
    val androidVersion: String,
    val totalRamGb: Int,
    val freeStorageGb: Int,
    val virtualization: String,
    val backend: String,
    val explanation: String
)

private object HostProbe {
    fun inspect(context: Context): HostReport {
        val memory = ActivityManager.MemoryInfo()
        (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .getMemoryInfo(memory)

        val stat = StatFs(Environment.getDataDirectory().absolutePath)
        val freeBytes = stat.availableBlocksLong * stat.blockSizeLong

        // These nodes are only hints. On stock Android, app sandbox permissions
        // generally prevent using them even if a node happens to exist.
        val nodes = listOf("/dev/kvm", "/dev/gunyah", "/dev/gzvm")
            .filter { File(it).exists() }

        val nodeSummary = if (nodes.isEmpty()) {
            "No KVM/Gunyah device node visible"
        } else {
            "Device node(s) visible: ${nodes.joinToString()}; app access is unverified"
        }

        val abi = Build.SUPPORTED_ABIS.firstOrNull() ?: "unknown"
        val arm64 = abi.equals("arm64-v8a", ignoreCase = true)
        val ramGb = (memory.totalMem / (1024L * 1024L * 1024L)).toInt()
        val freeGb = (freeBytes / (1024L * 1024L * 1024L)).toInt()

        return HostReport(
            cpuAbi = abi,
            androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
            totalRamGb = ramGb,
            freeStorageGb = freeGb,
            virtualization = nodeSummary,
            backend = if (arm64) "QEMU x86 TCG (software emulation on ARM64 host)" else "Unsupported host ABI",
            explanation = if (arm64) {
                "Windows 7 x86 runs as a different CPU architecture from this ARM64 phone. Rootless mode therefore uses QEMU TCG software translation; expect slow installation and desktop performance."
            } else {
                "This build requires a 64-bit ARM Android host."
            }
        )
    }
}

@Composable
private fun WindroidHome(report: HostReport, onSetup: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Windroid", style = MaterialTheme.typography.headlineLarge)
        Text("Rootless virtual ThinkPad • Windows 7 32-bit")
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Host device", style = MaterialTheme.typography.titleLarge)
                InfoRow("CPU architecture", report.cpuAbi)
                InfoRow("Android", report.androidVersion)
                InfoRow("Installed RAM", "${report.totalRamGb} GB")
                InfoRow("Free internal storage", "${report.freeStorageGb} GB")
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Virtualization check", style = MaterialTheme.typography.titleLarge)
                Text(report.virtualization)
                Text("Selected backend: ${report.backend}")
                Text(report.explanation, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Virtual ThinkPad profile", style = MaterialTheme.typography.titleLarge)
                InfoRow("Guest OS", "Windows 7 32-bit (x86)")
                InfoRow("Virtual CPU", "2 cores (initial profile)")
                InfoRow("Guest RAM", "2 GB default; adjustable")
                InfoRow("Virtual disk", "64 GB expandable")
                InfoRow("Firmware", "Legacy BIOS (SeaBIOS planned)")
                InfoRow("TPM", "Not required by Windows 7")
                InfoRow("Graphics", "Basic virtual display; 3D acceleration not guaranteed")
            }
        }
        Button(onClick = onSetup, enabled = false, modifier = Modifier.fillMaxWidth()) {
            Text("Windows VM engine not integrated yet")
        }
        Text(
            "This screen performs host detection only. It does not claim to have started Windows. The QEMU x86 engine, SeaBIOS, virtual disk creation, Windows 7 installer workflow and input/display bridge still need to be integrated and tested on the S22 Ultra.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(1.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
