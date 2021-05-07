package com.example.stickyheader.adapter;

import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stickyheader.adapter.sticky.StickyItemOwner;

import org.jetbrains.annotations.NotNull;

public class StickyItemDecoration extends RecyclerView.ItemDecoration {

    private final StickyItemOwner stickyItemOwner; // todo use anchor strategy
    private int stickyHeaderHeight;

    public StickyItemDecoration(@NotNull RecyclerView recyclerView, @NonNull StickyItemOwner listener) {
        stickyItemOwner = listener;

        // On Sticky Header Click
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            public boolean onInterceptTouchEvent(@NotNull RecyclerView recyclerView, @NotNull MotionEvent motionEvent) {
                if (motionEvent.getY() <= stickyHeaderHeight) { // todo change this depending on current anchor
                    // Handle the clicks on the header here ...
                    return true;
                }
                return false;
            }

            public void onTouchEvent(@NotNull RecyclerView recyclerView, @NotNull MotionEvent motionEvent) {
            }

            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
    }

    private enum StickyItemAnchor {
        BOTTOM,
        TOP
    }

    @Override
    public void onDrawOver(@NotNull Canvas canvas, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);

        PositionInfo positionInfo = getPositionInfo(parent);
        if (positionInfo == null) {
            Log.d("myLog", "positionInfo: null");
            return;
        }
        Log.d("myLog", "positionInfo: " + positionInfo.toString());

        View currentHeader = stickyItemOwner.getStickyItemViewHolder().itemView;
        if (currentHeader == null) {
            return;
        }

        fixLayoutSize(positionInfo.stickyItemAnchor, parent, currentHeader);

        View childInContact = getChildInContact(parent, currentHeader, positionInfo.stickyItemAnchor);
        if (childInContact == null) {
            return;
        }

        if (stickyItemOwner.isStickyItem(parent.getChildAdapterPosition(childInContact))) {
            moveHeader(canvas, currentHeader, childInContact);
            return;
        }

        drawHeader(canvas, currentHeader, positionInfo.stickyItemAnchor);
    }

    private @Nullable
    PositionInfo getPositionInfo(@NotNull RecyclerView parent) {
        int stickyElementPosition = stickyItemOwner.getStickyItemPosition();

        View topChild = parent.getChildAt(0);
        if (topChild == null) {
            Log.d("myLog", "getPositionInfo topChild=null");
            return null;
        }
        int topChildPosition = parent.getChildAdapterPosition(topChild);
        if (topChildPosition == RecyclerView.NO_POSITION) {
            Log.d("myLog", "getPositionInfo topChildPosition=RecyclerView.NO_POSITION");
            return null;
        }

        if (stickyElementPosition < topChildPosition) {
            return new PositionInfo(topChildPosition, StickyItemAnchor.TOP);
        }

        /* Get the bottom element position and check if sticky header is below */

        View bottomChild = parent.getChildAt(parent.getChildCount() - 1);
        Log.d("myLog", "getPositionInfo parent.getChildCount()=" + (parent.getChildCount() - 1));
        if (bottomChild == null) {
            Log.d("myLog", "getPositionInfo bottomChild=null");
            return null;
        }
        int bottomChildPosition = parent.getChildAdapterPosition(bottomChild);
        if (bottomChildPosition == RecyclerView.NO_POSITION) {
            Log.d("myLog", "getPositionInfo bottomChildPosition=RecyclerView.NO_POSITION");
            return null;
        }

        if (stickyElementPosition > bottomChildPosition) {
            return new PositionInfo(bottomChildPosition, StickyItemAnchor.BOTTOM);
        }

        Log.d("myLog", "getPositionInfo SUCK IT");
        return null;
    }

    /**
     * Properly measures and layouts the top sticky header.
     *
     * @param parent ViewGroup: RecyclerView in this case.
     */
    private void fixLayoutSize(StickyItemAnchor itemAnchor, ViewGroup parent, View view) {
        // Specs for parent (RecyclerView)
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);

        // Specs for children (headers)
        int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, parent.getPaddingLeft() + parent.getPaddingRight(), view.getLayoutParams().width);
        int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, parent.getPaddingTop() + parent.getPaddingBottom(), view.getLayoutParams().height);

        view.measure(childWidthSpec, childHeightSpec);

        stickyHeaderHeight = view.getMeasuredHeight();
        if (itemAnchor == StickyItemAnchor.TOP) {
            Log.d("myLog", "fixLayoutSize: StickyItemAnchor.TOP");
            Log.d("myLog", "fixLayoutSize: x1=0,y1=0, x2=" + view.getMeasuredWidth() + ",y2=" + view.getMeasuredHeight());
            view.layout(
                0,
                0,
                view.getMeasuredWidth(),
                view.getMeasuredHeight()
            );
        } else if (itemAnchor == StickyItemAnchor.BOTTOM) {
            Log.d("myLog", "fixLayoutSize: StickyItemAnchor.BOTTOM");
            Log.d("myLog", "fixLayoutSize: x1=0,y1=" + (parent.getMeasuredHeight() - view.getMeasuredHeight()) + ", x2=" + view.getMeasuredWidth() + ",y2=" + parent.getMeasuredHeight());
            view.layout(
                0,
                parent.getMeasuredHeight() - view.getMeasuredHeight(),
                view.getMeasuredWidth(),
                parent.getMeasuredHeight()
            );
        } else {
            throw new RuntimeException("other anchors not supported");
        }
    }

    private View getChildInContact(RecyclerView parent, View currentHeader, StickyItemAnchor itemAnchor) {
        View childInContact = null;
        if (itemAnchor == StickyItemAnchor.BOTTOM) {
            Log.d("myLog", "getChildInContact BOTTOM");
            int contactPoint = currentHeader.getTop();
            for (int i = parent.getChildCount() - 1; i >= 0; i--) {
                View child = parent.getChildAt(i);
                if (child.getTop() < contactPoint) {
                    if (child.getBottom() >= contactPoint) {
                        childInContact = child;
                        break;
                    }
                }
            }
        } else if (itemAnchor == StickyItemAnchor.TOP) {
            Log.d("myLog", "getChildInContact TOP");
            int contactPoint = currentHeader.getBottom();
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                if (child.getBottom() > contactPoint) {
                    if (child.getTop() <= contactPoint) {
                        childInContact = child;
                        break;
                    }
                }
            }
        } else {
            throw new RuntimeException("other anchors not supported");
        }
        return childInContact;
    }

    private void moveHeader(Canvas canvas, View currentHeader, View nextHeader) {
        canvas.save();
        canvas.translate(0, nextHeader.getTop() - currentHeader.getHeight());
        currentHeader.draw(canvas);
        canvas.restore();
    }

    private void drawHeader(Canvas canvas, View header, StickyItemAnchor itemAnchor) {
        canvas.save();
        if (itemAnchor == StickyItemAnchor.TOP) {
            canvas.translate(0, 0);
        } else if (itemAnchor == StickyItemAnchor.BOTTOM) {
            canvas.translate(0, canvas.getHeight() - header.getHeight());
        } else {
            throw new RuntimeException("other anchors not supported");
        }

        Log.d("myLog", "drawHeader: x1=" + header.getLeft() + ",y1=" + header.getTop() + ", x2=" + header.getRight() + ",y2=" + header.getBottom());
        header.draw(canvas);
        canvas.restore();
    }

    private static class PositionInfo {
        public final int elementPosition;
        public final StickyItemAnchor stickyItemAnchor;

        public PositionInfo(int elementPosition, StickyItemAnchor stickyItemAnchor) {
            this.elementPosition = elementPosition;
            this.stickyItemAnchor = stickyItemAnchor;
        }

        @Override
        public String toString() {
            return "PositionInfo{" +
                "elementPosition=" + elementPosition +
                ", stickyItemAnchor=" + stickyItemAnchor +
                '}';
        }
    }

}