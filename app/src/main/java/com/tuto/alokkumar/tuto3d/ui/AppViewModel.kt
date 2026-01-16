package com.tuto.alokkumar.tuto3d.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tuto.alokkumar.tuto3d.data.model.ModelFileRepository
import com.tuto.alokkumar.tuto3d.data.model.getFileNameFromUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ModelFileRepository(application)

    private val _modelPath = MutableStateFlow<String?>(null)
    val modelPath = _modelPath.asStateFlow()

    private val _modelTitle = MutableStateFlow<String?>(null)
    val modelTitle = _modelTitle.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSuccess = MutableStateFlow<Boolean?>(null)
    val isSuccess = _isSuccess.asStateFlow()

    suspend fun setModelTitle(model: String){
        _isLoading.value = true
        _modelTitle.value = model
        delay(10000)
        _isLoading.value = false
    }

    fun loadModelFromUri(uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            _isSuccess.value = null

            try {
                val path = repository.copyUriToCache(uri)

                if (path != null) {
                    _modelPath.value = path
                    _modelTitle.value = getApplication<Application>().getFileNameFromUri(uri) ?: "Unknown"
                    _isSuccess.value = true
                } else {
                    _isSuccess.value = false
                }
            } catch (e: Exception) {
                _isSuccess.value = false
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.clearCache()
            }
            _modelPath.value = null
            _modelTitle.value = null
            _isSuccess.value = null
        }
    }
}