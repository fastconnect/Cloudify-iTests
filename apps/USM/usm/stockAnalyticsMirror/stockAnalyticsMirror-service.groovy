service {
	
	icon "icon.png"
	name "stockAnalyticsMirror"
	mirrorProcessingUnit {
		binaries "stockAnalyticsMirror" //can be a folder, or a war file   		
		sla {
				highlyAvailable false
				memoryCapacityPerContainer 256 
			}
	}
	
}
