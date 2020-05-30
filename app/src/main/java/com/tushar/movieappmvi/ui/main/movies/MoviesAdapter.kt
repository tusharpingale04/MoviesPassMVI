package com.tushar.movieappmvi.ui.main.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.tushar.movieappmvi.R
import com.tushar.movieappmvi.models.Movie
import com.tushar.movieappmvi.util.GenericViewHolder
import com.tushar.movieappmvi.util.ImagePathGenerator
import kotlinx.android.synthetic.main.row_movie_item.view.*

class MoviesAdapter(
    private val requestManager: RequestManager,
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val NO_MORE_RESULTS = -1
        const val MOVIE_ITEM = 0
        const val TAG = "MoviesAdapter"
    }

    private val noMovieModel = Movie(
        NO_MORE_RESULTS,
        "",
        "",
        0.0,
        "",
        "",
        "",
        0.0,
        0L,
        0L
    )

    private val diffCallback = object : DiffUtil.ItemCallback<Movie>() {

        override fun areItemsTheSame(oldItem: Movie, newItem: Movie) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem

    }

    private val differ = AsyncListDiffer(
        MovieRecyclerChangeCallBack(adapter = this),
        AsyncDifferConfig.Builder(diffCallback).build()
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            MOVIE_ITEM -> {
                return MoviesViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.row_movie_item,
                        parent,
                        false
                    ),
                    requestManager,
                    interaction
                )
            }
            NO_MORE_RESULTS -> {
                return GenericViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.row_no_more_results,
                        parent,
                        false
                    )
                )
            }
            else -> {
                return MoviesViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.row_movie_item,
                        parent,
                        false
                    ),
                    requestManager,
                    interaction
                )
            }
        }
    }

    internal inner class MovieRecyclerChangeCallBack(
        private val adapter: MoviesAdapter
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
        when (holder) {
            is MoviesViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (differ.currentList[position].id > -1) {
            return MOVIE_ITEM
        }
        return NO_MORE_RESULTS
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(movieList: List<Movie>?, isQueryExhausted: Boolean) {
        val newList = movieList?.toMutableList()
        if (isQueryExhausted)
            newList?.add(noMovieModel)
        differ.submitList(newList)
    }

    class MoviesViewHolder
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