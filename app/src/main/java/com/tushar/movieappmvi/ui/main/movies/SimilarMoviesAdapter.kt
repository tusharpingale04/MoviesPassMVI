package com.tushar.movieappmvi.ui.main.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.tushar.movieappmvi.R
import com.tushar.movieappmvi.models.SimilarMovie
import com.tushar.movieappmvi.util.ImagePathGenerator
import kotlinx.android.synthetic.main.row_similar_movies.view.*

class SimilarMoviesAdapter(private val requestManager: RequestManager) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SimilarMovie>() {

        override fun areItemsTheSame(oldItem: SimilarMovie, newItem: SimilarMovie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SimilarMovie, newItem: SimilarMovie): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return SimilarMoviesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_similar_movies,
                parent,
                false
            ),
            requestManager
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SimilarMoviesViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<SimilarMovie>) {
        differ.submitList(list)
    }

    class SimilarMoviesViewHolder
    constructor(
        itemView: View,
        private val requestManager: RequestManager
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: SimilarMovie) = requestManager
            .load(ImagePathGenerator.getImagePath(item.posterPath!!))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(itemView.item_img)
    }

}
