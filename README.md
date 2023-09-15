# Clipping Example #

This is a sample app that demonstrates the use of clipping applied on the Canvas, to generate
various shapes.

## App Preview ##

<img alt="Clipping Example App Preview" src="https://github.com/pawanharariya/Clipping-Example/assets/43620548/aeb9f46c-91fe-478f-8456-b7c9af6e54ff" width=240/>

## Clipping ##

Clipping is a way to define regions of an image, canvas or bitmap that are selectively drawn or not
drawn on the screen. Clipping also reduces overdraw. Overdraw is when a pixel on the screen is drawn
more than once to display the final image. Thus clipping
increases drawing performance. Consider a drawing of playing cards stacked on top of each other such
that some portion of each card is visible. Instead of drawing all cards and overlapping them, we
only draw the visible portion of each card and clip the rest of the portion. Clipping is also used
to create interesting effects and animations.

## Quick Reject ##

The `quickReject()` method allows to check whether a specified rectangle or path would lie
completely outside the currently visible regions i.e. clipped region, after all transformations have
been applied.

The quickReject() method is useful when we are constructing more complex drawings and need to do so
as fast as possible. With quickReject(), we can decide efficiently which objects we do not have to
draw at all, and there is no need to write our own intersection logic.

The `quickReject()` method returns `true` if the rectangle or path would not be visible at all on
the screen. For partial overlaps, you still have to do our own checking.

## Helpful Tips ##

1. Instead of calculating each coordinate/position to draw multiple shapes on canvas, we can shift
   the canvas itself. By doing so, we translate the coordinate system of the canvas and draw shapes
   from there. In this way we only require one coordinate i.e. the translated origin. We must
   however restore the canvas to original position, after drawing. This approach is simpler,
   efficient and less error prone.

2. The general idea behind clipping is that we first define a clipped region and then we only draw
   in that clipped region, to get desired shape. We don't draw out everything first and then clip
   unwanted regions as it is less efficient.