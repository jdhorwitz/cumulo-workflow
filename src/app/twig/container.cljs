
(ns app.twig.container
  (:require [recollect.macros :refer [deftwig]] [app.twig.user :refer [twig-user]]))

(deftwig
 twig-members
 (sessions users)
 (->> sessions
      (map (fn [[k session]] [k (get-in users [(:user-id session) :name])]))
      (into {})))

(deftwig
 twig-container
 (db session records)
 (let [logged-in? (some? (:user-id session))
       router (:router session)
       base-data {:logged-in? logged-in?,
                  :session session,
                  :count (:count db),
                  :reel-length (count records)}]
   (merge
    base-data
    (if logged-in?
      {:user (twig-user (get-in db [:users (:user-id session)])),
       :router (assoc
                router
                :data
                (case (:name router) :profile (twig-members (:sessions db) (:users db)) {})),
       :count (count (:sessions db))}
      nil))))
