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
                        # See the OpenStreetMap Tile Usage Policy at https://operations.osmfoundation.org/policies/tiles/
                        GeoLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png").withLabel("OSM")
                    ]).withFeature("attribution", u"© OpenStreetMap contributors")
                }).withProvided(
                    ProvidedMeta().withValue().withOverwrite()
                ).withElement(
                    StringType("location").withAnnotated()
                )
        ]).withCallable(False).withFeatures({"icon":"map"})

    def onProvideArgs(self, context):
        if "locations" in context.provide:
            locations = [
                AnnotatedValue("location1").withValueLabel("Location with actions").withValueDescription("Description of Location 1").withFeatures({
                    "geoPosition":GeoPosition(50.06143, 19.93658), "icon":"home", "iconColor":"FF0000", "iconWidth":50, "iconHeight":50}).withFeature(
                        "contextActions", ["ActionWithGeoMapViewLocation"]),
                AnnotatedValue("location2").withValueLabel("Location without actions").withValueDescription("Description of Location 2").withFeatures({
                    "geoPosition":GeoPosition(50.06253, 19.93768), "icon":"face", "iconColor":"000000", "iconWidth":30, "iconHeight":30})
            ]
            context.provided["locations"] = ProvidedValue().withValue(AnnotatedValue(locations))

class ActionWithGeoMapViewLocation(Action):
    def onConfigure(self):
        self.withLabel("View the location")
        # Must set withOverwrite to replace with the current value.
        self.withArgs([
            StringType("location").withAnnotated().withLabel("Location").withFeature("visible", False),
            NumberType("label").withLabel("Label").withProvided(
                ProvidedMeta().withValue().withOverwrite().withReadOnly().withDependency("location")),
            NumberType("description").withLabel("Description").withProvided(
                ProvidedMeta().withValue().withOverwrite().withReadOnly().withDependency("location")),
            NumberType("latitude").withLabel("Latitude").withProvided(
                ProvidedMeta().withValue().withOverwrite().withReadOnly().withDependency("location")),
            NumberType("longitude").withLabel("Longitude").withProvided(
                ProvidedMeta().withValue().withOverwrite().withReadOnly().withDependency("location")),
            ])
        self.withCallable(False).withFeatures({"visible":False, "cancelLabel":"Close", "icon":"map-marker"})
    def onProvideArgs(self, context):
        if "label" in context.provide:
            context.provided["label"] = ProvidedValue().withValue(context.current["location"].valueLabel)
        if "description" in context.provide:
            context.provided["description"] = ProvidedValue().withValue(context.current["location"].valueDescription)
        if "latitude" in context.provide:
            # TODO Fearures convertion
            #context.provided["latitude"] = ProvidedValue().withValue(context.current["location"].features["geoPosition"].latitude)
            context.provided["latitude"] = ProvidedValue().withValue(context.current["location"].features["geoPosition"]["latitude"])
        if "longitude" in context.provide:
            # TODO Fearures convertion
            #context.provided["longitude"] = ProvidedValue().withValue(context.current["location"].features["geoPosition"].longitude)
            context.provided["longitude"] = ProvidedValue().withValue(context.current["location"].features["geoPosition"]["longitude"])
