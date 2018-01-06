/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2017-2018 Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jraf.routetools.lib.tools.formatter

import com.mapbox.services.commons.models.Position
import org.jraf.routetools.lib.model.Speed
import org.jraf.routetools.lib.tools.bearing.BearingInfer


object HtmlFormatter : Formatter {
    override fun format(positionList: List<Position>, speed: Speed, delayBetweenPositionsSecond: Int): String {
        val res = StringBuilder("""<!DOCTYPE html>
<html>
  <head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
    <title>Map</title>
    <style>
      #map {
        height: 100%;
      }

      html, body {
        height: 100%;
        margin: 0;
        padding: 0;
      }
    </style>
  </head>

  <body>
    <div id="map"></div>
    <canvas id="c" width="32" height="32"></canvas>

    <script>

function drawArrow(ctx, fromX, fromY, toX, toY) {
  var headLen = 8;
  var dx = toX - fromX;
  var dy = toY - fromY;
  var angle = Math.atan2(dy, dx);
  ctx.beginPath()
  ctx.moveTo(fromX, fromY);
  ctx.lineTo(toX, toY);
  ctx.lineTo(toX - headLen * Math.cos(angle - Math.PI / 6), toY - headLen * Math.sin(angle - Math.PI / 6));
  ctx.moveTo(toX, toY);
  ctx.lineTo(toX - headLen * Math.cos(angle + Math.PI / 6), toY - headLen * Math.sin(angle + Math.PI / 6));
  ctx.stroke();
}

function drawArrowAngle(ctx, angleDeg) {
  var angleRad = angleDeg * (Math.PI / 180);
  drawArrow(ctx, 16, 16, 16 + 16 * Math.cos(angleRad), 16 - 16 * Math.sin(angleRad));
}

var points = [
""")
        val positionAheadIndex = BearingInfer.DEFAULT_POSITION_COUNT / 2
        val bearingInfer = BearingInfer()
        for ((index, position) in positionList.withIndex()) {
            if (index + positionAheadIndex < positionList.lastIndex) {
                bearingInfer.add(positionList[index + positionAheadIndex])
            }
            res.append("{lat: ${position.latitude}, lng: ${position.longitude}, bear: ${bearingInfer.bearing}},\n")
        }

        res.append("""]

function initMap() {
  var map = new google.maps.Map(document.getElementById('map'), {
    zoom: 18,
    center: points[0]
  });

  var canvas = document.getElementById("c");
  var ctx = canvas.getContext("2d");

  for (var i = 0; i < points.length; i++) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.strokeStyle = "#FFFFFF";
    ctx.lineWidth = 6;
    drawArrowAngle(ctx, 90 - points[i].bear);

    ctx.strokeStyle = "#FF0000";
    ctx.lineWidth = 1;
    drawArrowAngle(ctx, 90 - points[i].bear);

    var imageUrl = canvas.toDataURL("image/png");
    var image = {
      url: imageUrl,
      size: new google.maps.Size(32, 32),
      origin: new google.maps.Point(0, 0),
      anchor: new google.maps.Point(16, 16)
    };
    new google.maps.Marker({
      position: points[i],
      map: map,
      icon: image
    });
  }
}
    </script>

    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDI-Ul38Luv1xUh5u_JXBNQS3smthektuU&callback=initMap">
    </script>

  </body>
</html>""")
        return res.toString()
    }
}