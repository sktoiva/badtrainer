(ns badtrainer.views)

(defn index []
  (str
   "<!doctype html>
    <html lang=\"en\">
    <head>
    <meta charset=\"utf-8\">
    <link href=\"css/screen.css\" rel=\"stylesheet\" type=\"text/css\">

     </head>
     <body>
       <div id=\"app\"></div>
       <script src=\"js/compiled/app.js\"></script>
       <script>badtrainer.core.init();</script>
     </body>
   </html>"))
