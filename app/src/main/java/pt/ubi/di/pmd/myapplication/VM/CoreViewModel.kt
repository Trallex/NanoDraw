package pt.ubi.di.pmd.myapplication.VM

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.divyanshu.draw.widget.MyPath
import com.divyanshu.draw.widget.PaintOptions
import pt.ubi.di.pmd.myapplication.DEFAULT_STROKE

class CoreViewModel: ViewModel() {
    fun savePaths(paths: LinkedHashMap<MyPath, PaintOptions>){
        drawning.value = paths
    }
    val drawning = MutableLiveData<LinkedHashMap<MyPath, PaintOptions>>()
    val color = MutableLiveData<Int>()
    val brushSize = MutableLiveData<Float>().apply {
        if(value == null) value = DEFAULT_STROKE
    }

}