package eu.mcomputing.mobv.zadanie.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.adapters.FeedItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {
    private val _feed_items = MutableLiveData<List<FeedItem>>()
    val feed_items: LiveData<List<FeedItem>> get() = _feed_items
    fun updateItems() {
        viewModelScope.launch {
            val new_items = fetchRandomNumber()
            _feed_items.postValue(new_items)
        }
    }
}

suspend fun fetchRandomNumber(): List<FeedItem> {
    delay(5000)
    val items = mutableListOf<FeedItem>()
    for (i in 1..100) {
        items.add(FeedItem(i, R.drawable.ic_feed, "Text ${(0..100).random()}"))
    }
    return items
}