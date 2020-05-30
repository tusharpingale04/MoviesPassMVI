package com.tushar.movieappmvi.ui.main.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.tushar.movieappmvi.R
import com.tushar.movieappmvi.models.Movie
import com.tushar.movieappmvi.util.ImagePathGenerator
import kotlinx.android.synthetic.main.row_movie_item.view.*

class FavoritesAdapter(
    private val requestManager: RequestManager,
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TAG = "FavoritesAdapter"
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Movie>() {

        override fun areItemsTheSame(oldItem: Movie, newItem: Movie) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem

    }

    private val differ = AsyncListDiffer(
        MovieRecyclerChangeCallBack(adapter = this),
        AsyncDifferConfig.Builder(diffCallback).build()
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FavoritesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_favorite_item,
                parent,
                false
            ),
            requestManager,
            interaction
        )
    }

    internal inner class MovieRecyclerChangeCallBack(
        private val adapter: FavoritesAdapter
    ) : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is FavoritesViewHolder ->{
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(movieList: List<Movie>?) {
        differ.submitList(movieList)
    }

    class FavoritesViewHolder
    constructor(
        itemView: View,
        private val requestManager: RequestManager,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Movie) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(absoluteAdapterPosition, item)
            }
            requestManager
                .load(ImagePathGenerator.getImagePath(item.posterPath!!))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(itemView.image)
            itemView.date.text = item.releaseDate
            itemView.ratingBar.rating = (item.voteAverage / 2).toFloat()
            itemView.title.text = item.originalTitle
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Movie)
    }
}