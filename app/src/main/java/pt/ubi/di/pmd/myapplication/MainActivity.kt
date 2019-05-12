package pt.ubi.di.pmd.myapplication

import android.Manifest
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.save_file.view.*
import pt.ubi.di.pmd.myapplication.VM.CoreViewModel
import java.io.File
import java.io.FileOutputStream
import java.security.Permission
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: CoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(CoreViewModel::class.java)
        bindUI()
    }
    private fun bindUI(){
        this.title = "NanoDraw"
        viewModel.drawning.observe(this, Observer {
            draw_view.mPaths = it
        })
        viewModel.stroke.observe(this, Observer {
            draw_view.setStrokeWidth(it)
        })
        viewModel.color.observe(this, Observer {
            draw_view.setColor(it)
        })
        button_settings.setOnClickListener {
            //TODO: settings ONCLICK iksde
        }
        button_undo.setOnClickListener{
            draw_view.undo()
        }
        button_redo.setOnClickListener {
            draw_view.redo()
        }
        button_save.setOnClickListener {
            saveImage()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.savePaths(draw_view.mPaths)
    }

    private fun saveImage() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener{
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    showSaveDialog()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(this@MainActivity, "No permission to save", Toast.LENGTH_SHORT).show()
                }
            }).check()
    }

    private fun showSaveDialog() {
        val view = layoutInflater.inflate(R.layout.save_file, findViewById(android.R.id.content), false)
            .apply {
                val file="NanoDrawing${SimpleDateFormat("yyyy.MM.dd-HH:mm:ss").format(Calendar.getInstance().time)}".trim()
                editTextFile.setSelectAllOnFocus(true)
                editTextFile.setText(file)
            }
        val builder =AlertDialog.Builder(this)
            .apply {
                setView(view)
                setTitle("Save the picture")
                setPositiveButton("Confirm"){_, _ -> saveFile(view.editTextFile.text.toString())}
                setNegativeButton("Cancel"){_, _ -> {Toast.makeText(this@MainActivity,"Canceled", Toast.LENGTH_SHORT).show()}}
            }
        val dialog = builder.create()
            .apply { window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) }
            .show()
    }

    private fun saveFile(filename: String) {
        val dir = "${Environment.DIRECTORY_PICTURES}/NanoDraw/"
        val path = Environment.getExternalStoragePublicDirectory(dir)
        val file = File(path, "$filename.jpg")
        path.mkdirs()
        file.createNewFile()
        val outputStream = FileOutputStream(file)
        val bitmap = draw_view.getBitmap()
            .apply {
                compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        outputStream.flush()
        outputStream.close()
        MediaScannerConnection.scanFile(this, arrayOf(file.toString()), null, null)

    }
}
