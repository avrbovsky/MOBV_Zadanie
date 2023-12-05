//package eu.mcomputing.mobv.zadanie.utils
//
//import android.content.Context
//import android.graphics.Bitmap
//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.DataSource
//import com.bumptech.glide.load.engine.GlideException
//import com.bumptech.glide.request.RequestListener
//import com.bumptech.glide.request.target.Target
//import com.google.android.gms.maps.model.AdvancedMarkerOptions
//import com.google.android.gms.maps.model.BitmapDescriptorFactory
//import com.google.android.gms.maps.model.Marker
//import eu.mcomputing.mobv.zadanie.R
//
//fun AdvancedMarkerOptions.loadIcon(context: Context, url: String?) {
//    Glide.with(context)
//        .asBitmap()
//        .load(url)
//        .error(R.drawable.ic_account_box)
//        .listener(object : RequestListener<Bitmap> {
//            override fun onLoadFailed(
//                e: GlideException?,
//                model: Any?,
//                target: Target<Bitmap>,
//                isFirstResource: Boolean
//            ): Boolean {
//                return false
//            }
//
//            override fun onResourceReady(
//                resource: Bitmap,
//                model: Any,
//                target: Target<Bitmap>?,
//                dataSource: DataSource,
//                isFirstResource: Boolean
//            ): Boolean {
//                return resource?.let {
//                    BitmapDescriptorFactory.fromBitmap(it)
//                }?.let {
//                    icon(it); true
//                } ?: false
//            }
//        }).submit()
//}