"""
Sponge Knowledge base
Demo - A geo map
"""

class ActionWithGeoMap(Action):
    def onConfigure(self):
        self.withLabel("Action with a geo map")
        self.withArgs([
            ListType("locations").withLabel("Locations").withAnnotated().withFeatures({
                    "geoMap":GeoMap().withCenter(GeoPosition(50.06143, 19.93658)).withZoom(15).withLayers([
                        # TODO A map is served locally in this example - OpenMapTiles Map Server in Docker [https://wiki.openstreetmap.org/wiki/OpenMapTiles].
                        GeoLayer("http://10.0.2.2:7070/styles/klokantech-basic/{z}/{x}/{y}.png").withOptions({
                            "attribution":
                                'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>'
                            })
                    ])
                }).withProvided(
                    ProvidedMeta().withValue().withOverwrite()
                ).withElement(
                    StringType("location").withAnnotated()
                )
        ]).withCallable(False).withFeatures({"icon":"map"})

    def onProvideArgs(self, context):
        if "locations" in context.provide:
            locations = [
                AnnotatedValue("location1").withValueLabel("Location 1").withFeatures({
                    "geoPosition":GeoPosition(50.06143, 19.93658), "icon":"home", "iconColor":"FF0000", "iconWidth":50, "iconHeight":50}),
                AnnotatedValue("location2").withValueLabel("Location 2").withFeatures({
                    "geoPosition":GeoPosition(50.06253, 19.93768), "icon":"face", "iconColor":"000000", "iconWidth":30, "iconHeight":30})
            ]
            context.provided["locations"] = ProvidedValue().withValue(AnnotatedValue(locations))
