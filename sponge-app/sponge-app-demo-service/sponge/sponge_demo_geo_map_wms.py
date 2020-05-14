"""
Sponge Knowledge base
Demo - A geo map
"""

GEO_CRS_ESPG_3413 = GeoCrs("EPSG:3413", "+proj=stere +lat_0=90 +lat_ts=70 +lon_0=-45 +k=1 +x_0=0 +y_0=0 +datum=WGS84 +units=m +no_defs")

class ActionWithGeoMapWms(Action):
    def onConfigure(self):
        self.withLabel("Action with a geo map WMS")
        self.withArgs([
            ListType("locations").withLabel("Locations").withAnnotated().withFeatures({
                    "geoMap":GeoMap().withCenter(GeoPosition(65.05166470332148, -19.171744826394896)).withZoom(3).withLayers([
                        # See the OpenStreetMap Tile Usage Policy at https://operations.osmfoundation.org/policies/tiles/
                        GeoTileLayer().withUrlTemplate("https://tile.openstreetmap.org/{z}/{x}/{y}.png").withLabel("OpenStreetMap")
                            .withFeatures({"visible":True, "attribution":u"© OpenStreetMap contributors"}),
                        GeoWmsLayer().withBaseUrl("https://www.gebco.net/data_and_products/gebco_web_services/north_polar_view_wms/mapserv?")
                            .withLayers(["gebco_north_polar_view"]).withCrs(GEO_CRS_ESPG_3413)
                            .withLabel("WMS (experimental)").withFeatures({"visible":True}),

                        GeoMarkerLayer("buildings").withLabel("Buildings").withFeature("icon", IconInfo().withName("home").withSize(50)),
                        GeoMarkerLayer("persons").withLabel("Persons")
                    ]).withCrs(GEO_CRS_ESPG_3413).withFeatures({"color":"FFFFFF"})
                }).withProvided(
                    ProvidedMeta().withValue().withOverwrite()
                ).withElement(
                    StringType("location").withAnnotated()
                )
        ]).withNonCallable().withFeatures({"icon":"map"})

    def onProvideArgs(self, context):
        if "locations" in context.provide:
            locations = [
                AnnotatedValue("building2").withValueLabel("Building").withValueDescription("Description of building").withFeatures({
                    "geoPosition":GeoPosition(65.05166470332148, -19.171744826394896),
                    "geoLayerName":"buildings"})
            ]
            context.provided["locations"] = ProvidedValue().withValue(AnnotatedValue(locations))

